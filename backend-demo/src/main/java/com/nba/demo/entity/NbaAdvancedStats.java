package com.nba.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NbaAdvancedStats {
    private Integer id;
    private Integer rk;
    private String player;
    private Integer age;
    private String team;
    private String pos;
    private Integer g;
    private Integer gs;
    private Integer mp;
    private BigDecimal per;
    private BigDecimal tsPct;
    private BigDecimal threePar;
    private BigDecimal ftr;
    private BigDecimal orbPct;
    private BigDecimal drbPct;
    private BigDecimal trbPct;
    private BigDecimal astPct;
    private BigDecimal stlPct;
    private BigDecimal blkPct;
    private BigDecimal tovPct;
    private BigDecimal usgPct;
    private BigDecimal ows;
    private BigDecimal dws;
    private BigDecimal ws;
    private BigDecimal ws48;
    private BigDecimal obpm;
    private BigDecimal dbpm;
    private BigDecimal bpm;
    private BigDecimal vorp;
    private String awards;
    private String playerAdditional;
    private LocalDateTime createdAt;
    
    private String playerImage;
    private Integer playerRank;
}
