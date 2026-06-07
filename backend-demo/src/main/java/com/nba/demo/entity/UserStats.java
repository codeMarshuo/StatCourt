package com.nba.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserStats {
    private Integer id;
    private String username;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer predictionCount;
    private String favoriteTeam;
    private String favoritePlayer;
    private LocalDate lastLoginDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    
    public Integer getPredictionCount() { return predictionCount; }
    public void setPredictionCount(Integer predictionCount) { this.predictionCount = predictionCount; }
    
    public String getFavoriteTeam() { return favoriteTeam; }
    public void setFavoriteTeam(String favoriteTeam) { this.favoriteTeam = favoriteTeam; }
    
    public String getFavoritePlayer() { return favoritePlayer; }
    public void setFavoritePlayer(String favoritePlayer) { this.favoritePlayer = favoritePlayer; }
    
    public LocalDate getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
