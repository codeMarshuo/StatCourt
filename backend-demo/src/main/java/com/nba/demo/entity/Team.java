package com.nba.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Team {
    private Integer teamid;
    
    @JsonProperty("teamrank")
    private Integer teamrank;
    
    @JsonProperty("teamimage")
    private String teamimage;
    
    @JsonProperty("Team")
    private String Team;
    
    @JsonProperty("NumberOfMatches")
    private Integer NumberOfMatches;
    
    @JsonProperty("win")
    private Integer win;
    
    @JsonProperty("lose")
    private Integer lose;
    
    @JsonProperty("winper")
    private String winper;
    
    @JsonProperty("Score")
    private Float Score;
    
    @JsonProperty("Rebound")
    private Float Rebound;
    
    @JsonProperty("Assist")
    private Float Assist;
    
    @JsonProperty("Steal")
    private Float Steal;
    
    @JsonProperty("Block")
    private Float Block;
    
    @JsonProperty("FieldGoalPer")
    private String FieldGoalPer;
    
    @JsonProperty("ThreePointShootingPer")
    private String ThreePointShootingPer;
    
    @JsonProperty("FreeThrowShootingPer")
    private String FreeThrowShootingPer;
    
    private LocalDateTime entry_time;
    private LocalDateTime update_time;
}
