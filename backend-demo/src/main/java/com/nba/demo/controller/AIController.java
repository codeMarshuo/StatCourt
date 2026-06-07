package com.nba.demo.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nba.demo.common.Result;
import com.nba.demo.service.SearchService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIController {

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    @Value("${ai.default-model:glm-4.7-flash}")
    private String defaultModel;

    @Autowired
    private SearchService searchService;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final org.springframework.http.MediaType TEXT_PLAIN_UTF8 = 
            new org.springframework.http.MediaType(org.springframework.http.MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);

    private static final Map<String, ModelInfo> AVAILABLE_MODELS = new LinkedHashMap<>();

    static {
        AVAILABLE_MODELS.put("glm-4.7-flash", new ModelInfo("glm-4.7-flash", "GLM-4.7-Flash", "快速文本模型", true, false));
    }

    @GetMapping("/models")
    public Result<List<ModelInfo>> getModels() {
        return Result.success(new ArrayList<>(AVAILABLE_MODELS.values()));
    }

    @PostMapping(value = "/chat", produces = "application/json;charset=UTF-8")
    public Result<String> chat(@RequestBody ChatRequest request) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return Result.success(generateLocalResponse(request.getMessage()));
            }

            String model = request.getModel() != null ? request.getModel() : defaultModel;
            String response = callLLMApi(request.getMessage(), request.getHistory(), model, request.getImageUrl());
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.success(generateLocalResponse(request.getMessage()));
        }
    }

    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter streamChat(
            @RequestParam String message,
            @RequestParam(required = false) String history,
            @RequestParam(required = false, defaultValue = "glm-4.7-flash") String model,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false, defaultValue = "false") boolean webSearch) {
        
        System.out.println("=== Stream API Called ===");
        System.out.println("model: " + model);
        System.out.println("webSearch: " + webSearch);
        System.out.println("imageUrl: " + (imageUrl != null ? "provided" : "null"));
        System.out.println("message: " + message);
        
        SseEmitter emitter = new SseEmitter(180000L);

        executorService.execute(() -> {
            try {
                if (apiKey == null || apiKey.isEmpty()) {
                    String response = generateLocalResponse(message);
                    emitter.send(SseEmitter.event()
                            .name("content")
                            .data(response, TEXT_PLAIN_UTF8));
                    emitter.complete();
                    return;
                }

                String enhancedMessage = message;
                if (webSearch) {
                    System.out.println(">>> Web search is enabled, calling searchService...");
                    emitter.send(SseEmitter.event()
                            .name("searching")
                            .data("🔍 正在联网搜索...", TEXT_PLAIN_UTF8));
                    
                    try {
                        String searchResults = searchService.searchForNBA(message);
                        if (searchResults != null && !searchResults.isEmpty()) {
                            System.out.println(">>> Search results found");
                            enhancedMessage = message + "\n\n" + searchResults + "\n\n请基于以上搜索结果回答用户问题。";
                        } else {
                            System.out.println(">>> No search results, using original message");
                        }
                    } catch (Exception searchEx) {
                        System.out.println(">>> Search failed: " + searchEx.getMessage());
                    }
                }

                streamLLMApi(enhancedMessage, history, model, imageUrl, emitter);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    String response = generateLocalResponse(message);
                    emitter.send(SseEmitter.event()
                            .name("content")
                            .data(response, TEXT_PLAIN_UTF8));
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            }
        });

        return emitter;
    }

    private void streamLLMApi(String message, String history, String model, String imageUrl, SseEmitter emitter) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("stream", true);
        requestBody.put("max_tokens", 65536);
        requestBody.put("temperature", 1.0);

        ModelInfo modelInfo = AVAILABLE_MODELS.getOrDefault(model, AVAILABLE_MODELS.get(defaultModel));
        if (modelInfo != null && modelInfo.isThinkingEnabled()) {
            JSONObject thinking = new JSONObject();
            thinking.put("type", "enabled");
            requestBody.put("thinking", thinking);
        }

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", getSystemPrompt());
        messages.add(systemMessage);

        if (history != null && !history.isEmpty()) {
            JSONArray historyArray = JSONArray.parseArray(history);
            for (int i = 0; i < historyArray.size(); i++) {
                messages.add(historyArray.getJSONObject(i));
            }
        }

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        
        boolean isMultimodal = modelInfo != null && modelInfo.isMultimodal();
        boolean hasImage = imageUrl != null && !imageUrl.isEmpty();
        
        System.out.println(">>> isMultimodal: " + isMultimodal + ", hasImage: " + hasImage);
        
        if (hasImage && isMultimodal) {
            System.out.println(">>> Building multimodal message with image");
            JSONArray content = new JSONArray();
            
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrlObj = new JSONObject();
            imageUrlObj.put("url", imageUrl);
            imageContent.put("image_url", imageUrlObj);
            content.add(imageContent);
            
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", message);
            content.add(textContent);
            
            userMessage.put("content", content);
        } else {
            userMessage.put("content", message);
        }
        messages.add(userMessage);

        requestBody.put("messages", messages);
        
        System.out.println(">>> Request body: " + requestBody.toJSONString().substring(0, Math.min(500, requestBody.toJSONString().length())) + "...");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .post(okhttp3.RequestBody.create(requestBody.toJSONString(), MediaType.parse("application/json; charset=UTF-8")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(">>> Response code: " + response.code());
            if (response.isSuccessful() && response.body() != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder thinkingContent = new StringBuilder();

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

                                if (delta != null) {
                                    if (delta.containsKey("reasoning_content")) {
                                        String reasoning = delta.getString("reasoning_content");
                                        if (reasoning != null && !reasoning.isEmpty()) {
                                            thinkingContent.append(reasoning);
                                            emitter.send(SseEmitter.event()
                                                    .name("thinking")
                                                    .data(reasoning, TEXT_PLAIN_UTF8));
                                        }
                                    }

                                    if (delta.containsKey("content")) {
                                        String content = delta.getString("content");
                                        if (content != null && !content.isEmpty()) {
                                            emitter.send(SseEmitter.event()
                                                    .name("content")
                                                    .data(content, TEXT_PLAIN_UTF8));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(">>> Parse error: " + e.getMessage());
                        }
                    }
                }

                if (thinkingContent.length() > 0) {
                    emitter.send(SseEmitter.event()
                            .name("thinking_complete")
                            .data("", TEXT_PLAIN_UTF8));
                }

                emitter.send(SseEmitter.event().name("done").data("", TEXT_PLAIN_UTF8));
                emitter.complete();
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                System.out.println(">>> API Error: " + errorBody);
                
                if (response.code() == 429) {
                    emitter.send(SseEmitter.event()
                            .name("content")
                            .data("⚠️ API请求频率限制，请稍后再试。\n\n", TEXT_PLAIN_UTF8));
                }
                
                String localResponse = generateLocalResponse(message);
                emitter.send(SseEmitter.event()
                        .name("content")
                        .data(localResponse, TEXT_PLAIN_UTF8));
                emitter.send(SseEmitter.event().name("done").data("", TEXT_PLAIN_UTF8));
                emitter.complete();
            }
        } catch (Exception e) {
            System.out.println(">>> Exception: " + e.getMessage());
            e.printStackTrace();
            String localResponse = generateLocalResponse(message);
            emitter.send(SseEmitter.event()
                    .name("content")
                    .data(localResponse, TEXT_PLAIN_UTF8));
            emitter.send(SseEmitter.event().name("done").data("", TEXT_PLAIN_UTF8));
            emitter.complete();
        }
    }

    private String callLLMApi(String message, String history, String model, String imageUrl) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 65536);
        requestBody.put("temperature", 1.0);

        ModelInfo modelInfo = AVAILABLE_MODELS.getOrDefault(model, AVAILABLE_MODELS.get(defaultModel));
        if (modelInfo != null && modelInfo.isThinkingEnabled()) {
            JSONObject thinking = new JSONObject();
            thinking.put("type", "enabled");
            requestBody.put("thinking", thinking);
        }

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", getSystemPrompt());
        messages.add(systemMessage);

        if (history != null && !history.isEmpty()) {
            JSONArray historyArray = JSONArray.parseArray(history);
            for (int i = 0; i < historyArray.size(); i++) {
                messages.add(historyArray.getJSONObject(i));
            }
        }

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        
        if (imageUrl != null && !imageUrl.isEmpty() && modelInfo != null && modelInfo.isMultimodal()) {
            JSONArray content = new JSONArray();
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrlObj = new JSONObject();
            imageUrlObj.put("url", imageUrl);
            imageContent.put("image_url", imageUrlObj);
            content.add(imageContent);
            
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", message);
            content.add(textContent);
            
            userMessage.put("content", content);
        } else {
            userMessage.put("content", message);
        }
        messages.add(userMessage);

        requestBody.put("messages", messages);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .post(okhttp3.RequestBody.create(requestBody.toJSONString(), MediaType.parse("application/json; charset=UTF-8")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = JSONObject.parseObject(responseBody);
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject msg = choice.getJSONObject("message");

                    StringBuilder result = new StringBuilder();

                    if (msg.containsKey("reasoning_content") && msg.getString("reasoning_content") != null) {
                        String thinking = msg.getString("reasoning_content");
                        result.append("> **💭 思考过程**\n\n");
                        result.append("```\n").append(thinking).append("\n```\n\n");
                        result.append("---\n\n");
                    }

                    if (msg.containsKey("content")) {
                        result.append(msg.getString("content"));
                    }

                    return result.toString();
                }
            }
        }

        return generateLocalResponse(message);
    }

    private String getSystemPrompt() {
        return "你是StatCourt AI助手，一个专业的NBA数据分析助手。\n\n" +
                "你的职责：\n" +
                "1. 解答NBA相关的问题，包括球队、球员、比赛数据等\n" +
                "2. 提供专业的篮球数据分析见解\n" +
                "3. 解释NBA高级数据指标（如PER、WS、BPM、VORP、TS%、USG%等）\n" +
                "4. 分析球队战术和球员表现\n" +
                "5. 进行比赛预测和分析\n" +
                "6. 如果用户上传了图片，请仔细分析图片内容并回答相关问题\n\n" +
                "## 图片分析能力\n" +
                "当用户上传图片时，你需要：\n" +
                "- 识别图片中的内容（球员、比赛场景、数据图表等）\n" +
                "- 分析图片中的数据或场景\n" +
                "- 结合图片内容回答用户问题\n\n" +
                "## 预测分析能力\n" +
                "当用户询问比赛预测、MVP预测、季后赛预测等问题时，你需要：\n" +
                "- 基于用户提供的数据进行综合分析\n" +
                "- 考虑球队战绩、球员状态、历史交锋等因素\n" +
                "- 给出合理的预测概率和分析理由\n\n" +
                "## 回答要求\n" +
                "- 使用Markdown格式回复，支持表格、列表、代码块等\n" +
                "- 数据分析要客观专业\n" +
                "- 保持友好专业的语气\n" +
                "- 适当使用emoji让回复更生动";
    }

    private String generateLocalResponse(String message) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("排名") || lowerMessage.contains("standings")) {
            return "## 🏆 NBA球队排名\n\n" +
                    "请前往 **数据** 页面查看最新的球队排名信息。\n\n" +
                    "> 排名数据实时更新，展示各球队的战绩和胜率。";
        }

        if (lowerMessage.contains("预测") || lowerMessage.contains("predict")) {
            return "## 🎯 AI预测分析\n\n" +
                    "我可以基于NBA数据进行智能预测分析！\n\n" +
                    "### 预测类型\n\n" +
                    "1. **比赛预测** - 分析两支球队的对阵结果\n" +
                    "2. **MVP预测** - 预测赛季MVP热门人选\n" +
                    "3. **季后赛预测** - 预测季后赛走势\n\n" +
                    "> ⚠️ 预测结果仅供参考，基于数据分析得出。";
        }

        if (lowerMessage.contains("你好") || lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
            return "## 👋 您好！\n\n" +
                    "我是StatCourt AI助手，专注于NBA数据分析。\n\n" +
                    "### 我可以帮您：\n\n" +
                    "1. **📊 球队分析** - 查看球队战绩、数据统计\n" +
                    "2. **🌟 球员数据** - 球员统计、对比分析\n" +
                    "3. **🎯 比赛预测** - 基于深度学习的比赛预测\n" +
                    "4. **🖼️ 图片分析** - 上传图片，我来分析\n\n" +
                    "> 直接输入您想了解的内容即可！";
        }

        return "## 🤔 我理解您的问题\n\n" +
                "很抱歉，我目前主要专注于NBA数据分析领域。\n\n" +
                "### 您可以尝试问我：\n\n" +
                "- **\"分析湖人队本赛季表现\"**\n" +
                "- **\"比较詹姆斯和库里的数据\"**\n" +
                "- **\"预测下一场比赛\"**\n" +
                "- **上传图片让我分析**\n\n" +
                "> 💡 我支持 **Markdown** 格式回复！";
    }

    public static class ChatRequest {
        private String message;
        private String history;
        private String model;
        private String imageUrl;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getHistory() { return history; }
        public void setHistory(String history) { this.history = history; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class ModelInfo {
        private String id;
        private String name;
        private String description;
        private boolean thinkingEnabled;
        private boolean multimodal;

        public ModelInfo() {}

        public ModelInfo(String id, String name, String description, boolean thinkingEnabled, boolean multimodal) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.thinkingEnabled = thinkingEnabled;
            this.multimodal = multimodal;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isThinkingEnabled() { return thinkingEnabled; }
        public void setThinkingEnabled(boolean thinkingEnabled) { this.thinkingEnabled = thinkingEnabled; }
        public boolean isMultimodal() { return multimodal; }
        public void setMultimodal(boolean multimodal) { this.multimodal = multimodal; }
    }
}
