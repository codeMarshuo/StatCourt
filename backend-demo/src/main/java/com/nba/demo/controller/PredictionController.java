package com.nba.demo.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nba.demo.common.Result;
import com.nba.demo.entity.Team;
import com.nba.demo.mapper.TeamMapper;
import com.nba.demo.service.BasketballPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/prediction")
@CrossOrigin(origins = "*")
public class PredictionController {

    @Autowired
    private BasketballPredictionService predictionService;

    @Autowired
    private TeamMapper teamMapper;

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private static final org.springframework.http.MediaType TEXT_PLAIN_UTF8 = 
        org.springframework.http.MediaType.parseMediaType("text/plain;charset=UTF-8");

    @GetMapping("/teams")
    public Result getTeams() {
        List<Team> teams = teamMapper.selectAll();
        return Result.success(teams);
    }

    @GetMapping("/stream")
    public SseEmitter streamPrediction(
            @RequestParam String homeTeam,
            @RequestParam String awayTeam,
            @RequestParam(required = false) Double homeOdds,
            @RequestParam(required = false) Double awayOdds,
            @RequestParam(required = false) Double handicap,
            @RequestParam(required = false) Double totalLine,
            @RequestParam(required = false, defaultValue = "true") boolean aiAnalysis) {
        
        SseEmitter emitter = new SseEmitter(180000L);
        
        executor.execute(() -> {
            try {
                emitter.send(SseEmitter.event().name("status").data("正在分析比赛数据...", TEXT_PLAIN_UTF8));
                
                String prompt = predictionService.buildPredictionPrompt(
                    homeTeam, awayTeam, homeOdds, awayOdds, handicap, totalLine
                );
                
                if (prompt == null) {
                    emitter.send(SseEmitter.event().name("error").data("球队数据不存在", TEXT_PLAIN_UTF8));
                    emitter.complete();
                    return;
                }
                
                if (apiKey == null || apiKey.isEmpty()) {
                    emitter.send(SseEmitter.event().name("error").data("AI API密钥未配置", TEXT_PLAIN_UTF8));
                    emitter.complete();
                    return;
                }
                
                emitter.send(SseEmitter.event().name("status").data("AI正在分析预测...", TEXT_PLAIN_UTF8));
                
                StringBuilder fullResponse = new StringBuilder();
                streamAIPrediction(prompt, emitter, fullResponse);
                
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data("预测失败: " + e.getMessage(), TEXT_PLAIN_UTF8));
                } catch (IOException ioException) {
                }
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    private void streamAIPrediction(String prompt, SseEmitter emitter, StringBuilder fullResponse) {
        try {
            String jsonBody = "{\"model\":\"glm-4.7-flash\",\"messages\":[{\"role\":\"user\",\"content\":\"" + 
                escapeJson(prompt) + "\"}],\"stream\":true}";
            
            okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    if (response.code() == 429) {
                        emitter.send(SseEmitter.event().name("status").data("API速率限制，使用本地预测...", TEXT_PLAIN_UTF8));
                        JSONObject localPrediction = predictionService.generateLocalPrediction(prompt);
                        emitter.send(SseEmitter.event().name("prediction").data(localPrediction.toJSONString(), TEXT_PLAIN_UTF8));
                        emitter.send(SseEmitter.event().name("analysis").data("由于API速率限制，本次预测基于本地算法生成。\n\n", TEXT_PLAIN_UTF8));
                        emitter.send(SseEmitter.event().name("analysis").data(generateLocalAnalysis(localPrediction), TEXT_PLAIN_UTF8));
                        emitter.send(SseEmitter.event().name("done").data("预测完成", TEXT_PLAIN_UTF8));
                        emitter.complete();
                        return;
                    }
                    emitter.send(SseEmitter.event().name("error").data("AI请求失败: " + response.code(), TEXT_PLAIN_UTF8));
                    emitter.complete();
                    return;
                }

                if (response.body() != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                break;
                            }
                            try {
                                JSONObject json = JSONObject.parseObject(data);
                                JSONArray choices = json.getJSONArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");
                                    if (delta != null && delta.containsKey("content")) {
                                        String content = delta.getString("content");
                                        if (content != null && !content.isEmpty()) {
                                            fullResponse.append(content);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            
            String responseText = fullResponse.toString();
            JSONObject prediction = predictionService.parseAIResponse(responseText);
            
            if (prediction.containsKey("error")) {
                emitter.send(SseEmitter.event().name("error").data(prediction.getString("error"), TEXT_PLAIN_UTF8));
                emitter.complete();
                return;
            }
            
            prediction.put("homeTeam", prompt.contains("主队数据") ? extractTeamName(prompt, "主队数据") : "");
            prediction.put("awayTeam", prompt.contains("客队数据") ? extractTeamName(prompt, "客队数据") : "");
            
            emitter.send(SseEmitter.event().name("prediction").data(prediction.toJSONString(), TEXT_PLAIN_UTF8));
            
            emitter.send(SseEmitter.event().name("status").data("AI正在生成详细分析...", TEXT_PLAIN_UTF8));
            
            String analysisPrompt = buildAnalysisPrompt(prediction);
            streamAIAnalysis(analysisPrompt, emitter);
            
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().name("error").data("AI预测失败: " + e.getMessage(), TEXT_PLAIN_UTF8));
            } catch (IOException ioException) {
            }
            emitter.completeWithError(e);
        }
    }

    private void streamAIAnalysis(String prompt, SseEmitter emitter) {
        try {
            String jsonBody = "{\"model\":\"glm-4.7-flash\",\"messages\":[{\"role\":\"user\",\"content\":\"" + 
                escapeJson(prompt) + "\"}],\"stream\":true}";
            
            okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    if (response.code() == 429) {
                        emitter.send(SseEmitter.event().name("analysis").data("API速率限制，分析内容基于预测数据生成。\n\n", TEXT_PLAIN_UTF8));
                        String localAnalysis = generateAnalysisFromPrompt(prompt);
                        emitter.send(SseEmitter.event().name("analysis").data(localAnalysis, TEXT_PLAIN_UTF8));
                    }
                    emitter.send(SseEmitter.event().name("done").data("预测完成", TEXT_PLAIN_UTF8));
                    emitter.complete();
                    return;
                }

                if (response.body() != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                break;
                            }
                            try {
                                JSONObject json = JSONObject.parseObject(data);
                                JSONArray choices = json.getJSONArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");
                                    if (delta != null && delta.containsKey("content")) {
                                        String content = delta.getString("content");
                                        if (content != null && !content.isEmpty()) {
                                            emitter.send(SseEmitter.event().name("analysis").data(content, TEXT_PLAIN_UTF8));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            
            emitter.send(SseEmitter.event().name("done").data("分析完成", TEXT_PLAIN_UTF8));
            emitter.complete();
            
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().name("done").data("预测完成", TEXT_PLAIN_UTF8));
            } catch (IOException ioException) {
            }
            emitter.complete();
        }
    }

    private String generateAnalysisFromPrompt(String prompt) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("## 比赛分析\n\n");
        
        try {
            String homeTeam = extractTeamFromPrompt(prompt, "主队");
            String awayTeam = extractTeamFromPrompt(prompt, "客队");
            
            analysis.append("### 球队对比\n");
            analysis.append(String.format("- 主队：%s\n", homeTeam != null ? homeTeam : "主队"));
            analysis.append(String.format("- 客队：%s\n\n", awayTeam != null ? awayTeam : "客队"));
            
            analysis.append("### 关键因素\n");
            analysis.append("- 主场优势：主队通常有3-5分的优势\n");
            analysis.append("- 近期状态：建议关注双方近5场比赛表现\n");
            analysis.append("- 伤病情况：赛前需确认主力球员出战状态\n\n");
            
            analysis.append("### 观赛建议\n");
            analysis.append("- 关注双方核心球员的对位表现\n");
            analysis.append("- 注意临场因素如伤病、轮休等\n");
            analysis.append("- 比赛结果存在不确定性，仅供参考\n");
        } catch (Exception e) {
            analysis.append("由于数据限制，无法生成详细分析。\n");
        }
        
        return analysis.toString();
    }
    
    private String extractTeamFromPrompt(String prompt, String marker) {
        try {
            int idx = prompt.indexOf(marker + "数据");
            if (idx == -1) return null;
            int start = prompt.indexOf(": ", idx);
            if (start == -1) return null;
            start += 2;
            int end = prompt.indexOf("\n", start);
            return end == -1 ? prompt.substring(start).trim() : prompt.substring(start, end).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String buildAnalysisPrompt(JSONObject prediction) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的NBA篮球分析师。请根据以下比赛预测数据，提供详细的分析报告。\n\n");
        
        prompt.append("## 预测结果\n");
        
        JSONObject winLose = prediction.getJSONObject("winLose");
        if (winLose != null) {
            prompt.append("### 胜负预测\n");
            prompt.append("- 主胜概率: ").append(safeGetDouble(winLose, "homeWinProb")).append("%\n");
            prompt.append("- 客胜概率: ").append(safeGetDouble(winLose, "awayWinProb")).append("%\n");
            prompt.append("- 预测结果: ").append(winLose.getString("prediction")).append("\n");
            prompt.append("- 信心指数: ").append(safeGetDouble(winLose, "confidence")).append("%\n");
            prompt.append("\n");
        }
        
        JSONObject scoreDiff = prediction.getJSONObject("scoreDiff");
        if (scoreDiff != null) {
            prompt.append("### 胜分差预测\n");
            prompt.append("- 最可能分差: ").append(scoreDiff.getString("mostLikely")).append("\n\n");
        }
        
        prompt.append("### 加时概率\n");
        prompt.append("- 加时概率: ").append(safeGetDouble(prediction, "overtimeProbability")).append("%\n\n");
        
        prompt.append("请提供:\n");
        prompt.append("1. 比赛整体分析（球队实力对比、近期状态等）\n");
        prompt.append("2. 关键因素分析（主客场优势、进攻防守对比等）\n");
        prompt.append("3. 核心球员对位分析\n");
        prompt.append("4. 需要注意的风险因素\n\n");
        prompt.append("请用中文回答，格式清晰，适合普通用户阅读。仅从篮球竞技角度分析，不涉及任何投注内容。");
        
        return prompt.toString();
    }

    private String extractTeamName(String prompt, String marker) {
        try {
            int start = prompt.indexOf(marker);
            if (start == -1) return "";
            start = prompt.indexOf(": ", start) + 2;
            int end = prompt.indexOf("\n", start);
            if (end == -1) end = prompt.length();
            return prompt.substring(start, end).trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String safeGetDouble(JSONObject json, String key) {
        try {
            Object value = json.get(key);
            if (value == null) return "0";
            if (value instanceof Number) {
                return String.valueOf(((Number) value).doubleValue());
            }
            String strValue = value.toString();
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[-+]?[0-9]*\\.?[0-9]+").matcher(strValue);
            if (matcher.find()) {
                return matcher.group();
            }
            return "0";
        } catch (Exception e) {
            return "0";
        }
    }

    private String generateLocalAnalysis(JSONObject prediction) {
        StringBuilder analysis = new StringBuilder();
        
        JSONObject winLose = prediction.getJSONObject("winLose");
        if (winLose != null) {
            double homeProb = winLose.getDoubleValue("homeWinProb");
            double awayProb = winLose.getDoubleValue("awayWinProb");
            String predResult = winLose.getString("prediction");
            
            analysis.append("## 胜负分析\n");
            analysis.append(String.format("根据两队数据对比，主胜概率为%.1f%%，客胜概率为%.1f%%。\n", homeProb, awayProb));
            analysis.append(String.format("预测结果：**%s**\n\n", predResult));
            
            if (homeProb > 60) {
                analysis.append("主队整体实力占优，在攻防两端都有一定优势。\n\n");
            } else if (awayProb > 60) {
                analysis.append("客队实力较强，但主场因素可能带来一定变数。\n\n");
            } else {
                analysis.append("两队实力接近，胜负悬念较大，比赛可能非常激烈。\n\n");
            }
        }
        
        JSONObject scoreDiff = prediction.getJSONObject("scoreDiff");
        if (scoreDiff != null) {
            analysis.append("## 分差预测\n");
            analysis.append("最可能分差：").append(scoreDiff.getString("mostLikely")).append("\n\n");
        }
        
        analysis.append("## 关键因素\n");
        analysis.append("- 主场优势：主队通常有3-5分的心理优势\n");
        analysis.append("- 近期状态：建议关注双方近5场比赛表现\n");
        analysis.append("- 伤病情况：赛前需确认主力球员出战状态\n\n");
        
        analysis.append("## 风险提示\n");
        analysis.append("- 本预测基于球队基础数据，未考虑伤病、轮休等因素\n");
        analysis.append("- 篮球比赛存在较大不确定性，预测仅供参考\n");
        analysis.append("- 比赛结果受多种因素影响，请理性看待\n");
        
        return analysis.toString();
    }

    public static class PredictionRequest {
        private String homeTeam;
        private String awayTeam;
        private Double homeOdds;
        private Double awayOdds;
        private Double handicap;
        private Double totalLine;

        public String getHomeTeam() { return homeTeam; }
        public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
        public String getAwayTeam() { return awayTeam; }
        public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
        public Double getHomeOdds() { return homeOdds; }
        public void setHomeOdds(Double homeOdds) { this.homeOdds = homeOdds; }
        public Double getAwayOdds() { return awayOdds; }
        public void setAwayOdds(Double awayOdds) { this.awayOdds = awayOdds; }
        public Double getHandicap() { return handicap; }
        public void setHandicap(Double handicap) { this.handicap = handicap; }
        public Double getTotalLine() { return totalLine; }
        public void setTotalLine(Double totalLine) { this.totalLine = totalLine; }
    }
}
