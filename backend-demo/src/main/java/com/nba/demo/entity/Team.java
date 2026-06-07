package com.nba.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Team {
    private Integer teamid;
    private Integer teamrank;
    private String teamimage;
    
    private String Team;
    
    private Integer numberofmatches;
    
    private Integer win;
    
    private Integer lose;
    
    private String winper;
    
    private Double Score;
    
    private Double Rebound;
    
    private Double Assist;
    
    private Double Steal;
    
    private Double Block;
    
    private String FieldGoalPer;
    
    private String ThreePointShootingPer;
    
    private String FreeThrowShootingPer;
    
    private LocalDateTime entry_time;
    private LocalDateTime update_time;
    
    public String getTeamname() {
        return Team;
    }
    
    public String getWinper() {
        return winper;
    }
    
    public Double getScore() {
        return Score != null ? Score : 0.0;
    }
    
    public Double getRebound() {
        return Rebound != null ? Rebound : 0.0;
    }
    
    public Double getAssist() {
        return Assist != null ? Assist : 0.0;
    }
    
    public Double getSteal() {
        return Steal != null ? Steal : 0.0;
    }
    
    public Double getBlock() {
        return Block != null ? Block : 0.0;
    }
    
    public Integer getWin() {
        return win != null ? win : 0;
    }
    
    public Integer getLose() {
        return lose != null ? lose : 0;
    }
    
    public Double getWinRate() {
        if (winper != null && winper.contains("%")) {
            try {
                return Double.parseDouble(winper.replace("%", "")) / 100;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}
