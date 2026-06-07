package com.nba.demo.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface StreamCallback {
        void onContent(String content);
        void onComplete();
        void onError(String error);
    }

    public void streamChat(String message, String model, String imageUrl, StreamCallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onContent("AI服务未配置，请设置API密钥。");
            callback.onComplete();
            return;
        }

        try {
            String modelToUse = model != null ? model : "glm-4.7-flash";
            String jsonBody = buildRequestBody(message, null, modelToUse, imageUrl);

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    callback.onError("API请求失败: " + response.code());
                    return;
                }

                if (response.body() != null) {
                    String responseBody = response.body().string();
                    String content = parseContentFromResponse(responseBody);
                    callback.onContent(content);
                }
                callback.onComplete();
            }
        } catch (Exception e) {
            callback.onError("AI服务错误: " + e.getMessage());
        }
    }

    private String buildRequestBody(String message, String history, String model, String imageUrl) {
        StringBuilder json = new StringBuilder();
        json.append("{\"model\":\"").append(model).append("\",");
        json.append("\"messages\":[");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            json.append("{\"role\":\"user\",\"content\":[");
            json.append("{\"type\":\"text\",\"text\":\"").append(escapeJson(message)).append("\"},");
            json.append("{\"type\":\"image_url\",\"image_url\":{\"url\":\"").append(escapeJson(imageUrl)).append("\"}}");
            json.append("]}");
        } else {
            json.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(message)).append("\"}");
        }

        json.append("],\"stream\":false}");
        return json.toString();
    }

    private String parseContentFromResponse(String responseBody) {
        try {
            com.alibaba.fastjson2.JSONObject json = com.alibaba.fastjson2.JSONObject.parseObject(responseBody);
            com.alibaba.fastjson2.JSONArray choices = json.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                com.alibaba.fastjson2.JSONObject choice = choices.getJSONObject(0);
                com.alibaba.fastjson2.JSONObject message = choice.getJSONObject("message");
                if (message != null) {
                    return message.getString("content");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无法解析AI响应";
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
