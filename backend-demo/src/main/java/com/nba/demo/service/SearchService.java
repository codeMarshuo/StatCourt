package com.nba.demo.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
public class SearchService {

    @Value("${search.api.key:}")
    private String apiKey;

    @Value("${search.api.url:https://www.searchapi.io/api/v1/search}")
    private String apiUrl;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public String search(String query) {
        return search(query, "google");
    }

    public String search(String query, String engine) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "";
        }

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = apiUrl + "?engine=" + engine + "&q=" + encodedQuery + "&api_key=" + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseSearchResults(responseBody);
                } else {
                    System.out.println("SearchAPI error: " + response.code() + " - " + response.message());
                    return "";
                }
            }
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
            return "";
        }
    }

    private String parseSearchResults(String responseBody) {
        StringBuilder result = new StringBuilder();
        
        try {
            JSONObject json = JSONObject.parseObject(responseBody);
            
            JSONArray organicResults = json.getJSONArray("organic_results");
            if (organicResults != null && !organicResults.isEmpty()) {
                result.append("【联网搜索结果】\n\n");
                
                int count = Math.min(organicResults.size(), 5);
                for (int i = 0; i < count; i++) {
                    JSONObject item = organicResults.getJSONObject(i);
                    String title = item.getString("title");
                    String snippet = item.getString("snippet");
                    String link = item.getString("link");
                    
                    result.append((i + 1)).append(". ").append(title != null ? title : "无标题").append("\n");
                    if (snippet != null && !snippet.isEmpty()) {
                        result.append("   摘要: ").append(snippet).append("\n");
                    }
                    if (link != null && !link.isEmpty()) {
                        result.append("   来源: ").append(link).append("\n");
                    }
                    result.append("\n");
                }
            }
            
            JSONObject answerBox = json.getJSONObject("answer_box");
            if (answerBox != null) {
                String answer = answerBox.getString("answer");
                if (answer != null && !answer.isEmpty()) {
                    result.insert(0, "【快速回答】" + answer + "\n\n");
                }
            }
            
            if (result.length() == 0) {
                return "";
            }
            
        } catch (Exception e) {
            System.out.println("Parse search results error: " + e.getMessage());
            return "";
        }
        
        return result.toString();
    }

    public String searchForNBA(String query) {
        String nbaQuery = "NBA " + query + " 2025";
        return search(nbaQuery, "google");
    }
}
