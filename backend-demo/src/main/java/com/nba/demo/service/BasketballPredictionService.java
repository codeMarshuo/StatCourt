package com.nba.demo.service;

import com.alibaba.fastjson2.JSONObject;
import com.nba.demo.entity.Team;
import com.nba.demo.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

@Service
public class BasketballPredictionService {

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

    public JSONObject getTeamData(String teamName) {
        Team team = teamMapper.selectByName(teamName);
        if (team == null) {
            return null;
        }
        
        JSONObject data = new JSONObject();
        data.put("name", team.getTeamname());
        data.put("win", team.getWin());
        data.put("lose", team.getLose());
        data.put("winRate", team.getWinRate());
        data.put("score", team.getScore());
        data.put("rebound", team.getRebound());
        data.put("assist", team.getAssist());
        data.put("steal", team.getSteal());
        data.put("block", team.getBlock());
        data.put("fieldGoalPer", team.getFieldGoalPer());
        data.put("threePointPer", team.getThreePointShootingPer());
        data.put("freeThrowPer", team.getFreeThrowShootingPer());
        
        return data;
    }

    public String buildPredictionPrompt(String homeTeam, String awayTeam, 
                                        Double homeOdds, Double awayOdds,
                                        Double handicap, Double totalLine) {
        JSONObject homeData = getTeamData(homeTeam);
        JSONObject awayData = getTeamData(awayTeam);
        
        if (homeData == null || awayData == null) {
            return null;
        }
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的NBA篮球分析师和预测专家。请根据以下球队数据，对比赛进行预测分析。\n\n");
        
        prompt.append("## 主队数据: ").append(homeTeam).append("\n");
        prompt.append("- 胜负: ").append(homeData.getInteger("win")).append("胜").append(homeData.getInteger("lose")).append("负\n");
        prompt.append("- 胜率: ").append(Math.round(homeData.getDouble("winRate") * 100)).append("%\n");
        prompt.append("- 场均得分: ").append(homeData.getDouble("score")).append("\n");
        prompt.append("- 场均篮板: ").append(homeData.getDouble("rebound")).append("\n");
        prompt.append("- 场均助攻: ").append(homeData.getDouble("assist")).append("\n");
        prompt.append("- 场均抢断: ").append(homeData.getDouble("steal")).append("\n");
        prompt.append("- 场均盖帽: ").append(homeData.getDouble("block")).append("\n");
        prompt.append("- 投篮命中率: ").append(homeData.getString("fieldGoalPer")).append("\n");
        prompt.append("- 三分命中率: ").append(homeData.getString("threePointPer")).append("\n");
        prompt.append("- 罚球命中率: ").append(homeData.getString("freeThrowPer")).append("\n\n");
        
        prompt.append("## 客队数据: ").append(awayTeam).append("\n");
        prompt.append("- 胜负: ").append(awayData.getInteger("win")).append("胜").append(awayData.getInteger("lose")).append("负\n");
        prompt.append("- 胜率: ").append(Math.round(awayData.getDouble("winRate") * 100)).append("%\n");
        prompt.append("- 场均得分: ").append(awayData.getDouble("score")).append("\n");
        prompt.append("- 场均篮板: ").append(awayData.getDouble("rebound")).append("\n");
        prompt.append("- 场均助攻: ").append(awayData.getDouble("assist")).append("\n");
        prompt.append("- 场均抢断: ").append(awayData.getDouble("steal")).append("\n");
        prompt.append("- 场均盖帽: ").append(awayData.getDouble("block")).append("\n");
        prompt.append("- 投篮命中率: ").append(awayData.getString("fieldGoalPer")).append("\n");
        prompt.append("- 三分命中率: ").append(awayData.getString("threePointPer")).append("\n");
        prompt.append("- 罚球命中率: ").append(awayData.getString("freeThrowPer")).append("\n\n");
        
        if (homeOdds != null && awayOdds != null) {
            prompt.append("## 赔率信息\n");
            prompt.append("- 主胜赔率: ").append(homeOdds).append("\n");
            prompt.append("- 客胜赔率: ").append(awayOdds).append("\n\n");
        }
        
        if (handicap != null) {
            prompt.append("## 让分线: ").append(handicap).append("\n\n");
        }
        
        if (totalLine != null) {
            prompt.append("## 大小分线: ").append(totalLine).append("\n\n");
        }
        
        prompt.append("请提供以下预测结果（必须严格按照JSON格式返回，不要包含任何其他文字）：\n\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"winLose\": {\n");
        prompt.append("    \"homeWinProb\": 主胜概率(0-100的数字),\n");
        prompt.append("    \"awayWinProb\": 客胜概率(0-100的数字),\n");
        prompt.append("    \"prediction\": \"主胜或客胜\",\n");
        prompt.append("    \"confidence\": 信心指数(0-100的数字)\n");
        prompt.append("  },\n");
        prompt.append("  \"handicap\": {\n");
        prompt.append("    \"handicapLine\": 让分线,\n");
        prompt.append("    \"homeCoverProb\": 让分主胜概率,\n");
        prompt.append("    \"awayCoverProb\": 让分客胜概率,\n");
        prompt.append("    \"prediction\": \"让分主胜或让分客胜\",\n");
        prompt.append("    \"expectedDiff\": 预期分差\n");
        prompt.append("  },\n");
        prompt.append("  \"totalScore\": {\n");
        prompt.append("    \"totalLine\": 大小分线,\n");
        prompt.append("    \"expectedTotal\": 预期总分,\n");
        prompt.append("    \"overProb\": 大分概率,\n");
        prompt.append("    \"underProb\": 小分概率,\n");
        prompt.append("    \"prediction\": \"大分或小分\"\n");
        prompt.append("  },\n");
        prompt.append("  \"scoreDiff\": {\n");
        prompt.append("    \"mostLikely\": \"最可能分差区间\"\n");
        prompt.append("  },\n");
        prompt.append("  \"overtimeProbability\": 加时概率\n");
        prompt.append("}\n");
        prompt.append("```\n\n");
        prompt.append("注意：只返回JSON数据，不要包含任何解释性文字。");
        
        return prompt.toString();
    }

    public JSONObject parseAIResponse(String response) {
        try {
            String jsonStr = response;
            if (response.contains("```json")) {
                jsonStr = response.substring(response.indexOf("```json") + 7);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
            } else if (response.contains("```")) {
                jsonStr = response.substring(response.indexOf("```") + 3);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
            }
            
            jsonStr = jsonStr.trim();
            return JSONObject.parseObject(jsonStr);
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("error", "解析AI响应失败: " + e.getMessage());
            return error;
        }
    }

    public JSONObject generateLocalPrediction(String prompt) {
        JSONObject result = new JSONObject();
        
        try {
            String homeTeam = extractValue(prompt, "主队数据: ", "\n");
            String awayTeam = extractValue(prompt, "客队数据: ", "\n");
            
            JSONObject homeData = extractTeamDataFromPrompt(prompt, "主队数据");
            JSONObject awayData = extractTeamDataFromPrompt(prompt, "客队数据");
            
            double homeWinRate = homeData != null ? homeData.getDoubleValue("winRate") : 0.5;
            double awayWinRate = awayData != null ? awayData.getDoubleValue("winRate") : 0.5;
            double homeScore = homeData != null ? homeData.getDoubleValue("score") : 110;
            double awayScore = awayData != null ? awayData.getDoubleValue("score") : 110;
            
            double homeAdvantage = 0.05;
            double baseHomeProb = (homeWinRate + homeAdvantage) / (homeWinRate + awayWinRate + homeAdvantage * 2);
            double homeWinProb = Math.round(baseHomeProb * 1000) / 10.0;
            double awayWinProb = 100 - homeWinProb;
            
            JSONObject winLose = new JSONObject();
            winLose.put("homeWinProb", homeWinProb);
            winLose.put("awayWinProb", awayWinProb);
            winLose.put("prediction", homeWinProb > awayWinProb ? "主胜" : "客胜");
            winLose.put("confidence", Math.max(homeWinProb, awayWinProb));
            result.put("winLose", winLose);
            
            double expectedDiff = (homeScore - awayScore) * 0.8;
            JSONObject scoreDiff = new JSONObject();
            String diffRange = Math.abs(expectedDiff) < 5 ? "1-5分" : Math.abs(expectedDiff) < 10 ? "6-10分" : "11-15分";
            scoreDiff.put("mostLikely", (expectedDiff > 0 ? homeTeam : awayTeam) + "胜" + diffRange);
            result.put("scoreDiff", scoreDiff);
            
            result.put("overtimeProbability", 8.5);
            result.put("homeTeam", homeTeam);
            result.put("awayTeam", awayTeam);
            
        } catch (Exception e) {
            JSONObject winLose = new JSONObject();
            winLose.put("homeWinProb", 50);
            winLose.put("awayWinProb", 50);
            winLose.put("prediction", "胜负难料");
            winLose.put("confidence", 50);
            result.put("winLose", winLose);
            
            JSONObject scoreDiff = new JSONObject();
            scoreDiff.put("mostLikely", "分差接近");
            result.put("scoreDiff", scoreDiff);
            
            result.put("overtimeProbability", 8.5);
        }
        
        return result;
    }
    
    private String extractValue(String text, String start, String end) {
        try {
            int s = text.indexOf(start);
            if (s == -1) return "";
            s += start.length();
            int e = text.indexOf(end, s);
            return e == -1 ? text.substring(s).trim() : text.substring(s, e).trim();
        } catch (Exception e) {
            return "";
        }
    }
    
    private JSONObject extractTeamDataFromPrompt(String prompt, String marker) {
        try {
            JSONObject data = new JSONObject();
            String section = extractValue(prompt, marker + ":", "\n\n");
            if (section.isEmpty()) return null;
            
            data.put("winRate", extractDoubleFromText(section, "胜率:") / 100);
            data.put("score", extractDoubleFromText(section, "场均得分:"));
            data.put("rebound", extractDoubleFromText(section, "场均篮板:"));
            data.put("assist", extractDoubleFromText(section, "场均助攻:"));
            
            return data;
        } catch (Exception e) {
            return null;
        }
    }
    
    private double extractDoubleFromText(String text, String label) {
        try {
            int idx = text.indexOf(label);
            if (idx == -1) return 0;
            String num = text.substring(idx + label.length()).trim().split("[^0-9.]")[0];
            return Double.parseDouble(num);
        } catch (Exception e) {
            return 0;
        }
    }
}
