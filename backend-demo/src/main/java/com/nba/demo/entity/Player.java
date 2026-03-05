package com.nba.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Player {
    private Integer id;
    private Integer PlayerRank;
    private String PlayerImage;
    private String Surname;
    private String Name;
    private String Team;
    private Integer NumberOfMatches;
    private Float Time;
    private Float Score;
    private Float Rebound;
    private Float Assist;
    private Float Steal;
    private Float Block;
    private String FieldGoalPer;
    private Integer ThreePointFieldGoals;
    private String ThreePointShootingPer;
    private String FreeThrowShootingPer;
    private String PlayerName;
    private LocalDateTime entry_time;
    private LocalDateTime update_time;
}
