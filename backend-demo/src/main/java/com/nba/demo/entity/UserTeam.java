package com.nba.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserTeam {
    private Integer id;
    private Integer userId;
    private String teamName;
    private Integer pgId;
    private Integer sgId;
    private Integer sfId;
    private Integer pfId;
    private Integer cId;
    private BigDecimal totalScore;
    private BigDecimal totalRebound;
    private BigDecimal totalAssist;
    private BigDecimal totalPer;
    private BigDecimal totalWs;
    private BigDecimal totalBpm;
    private BigDecimal lineupRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String pgName;
    private String sgName;
    private String sfName;
    private String pfName;
    private String cName;
    private String pgTeam;
    private String sgTeam;
    private String sfTeam;
    private String pfTeam;
    private String cTeam;
    private String pgImage;
    private String sgImage;
    private String sfImage;
    private String pfImage;
    private String cImage;
    private Double pgScore;
    private Double sgScore;
    private Double sfScore;
    private Double pfScore;
    private Double cScore;
    private Double pgRebound;
    private Double sgRebound;
    private Double sfRebound;
    private Double pfRebound;
    private Double cRebound;
    private Double pgAssist;
    private Double sgAssist;
    private Double sfAssist;
    private Double pfAssist;
    private Double cAssist;
    private Double pgPer;
    private Double sgPer;
    private Double sfPer;
    private Double pfPer;
    private Double cPer;
    private Double pgWs;
    private Double sgWs;
    private Double sfWs;
    private Double pfWs;
    private Double cWs;
    private Double pgBpm;
    private Double sgBpm;
    private Double sfBpm;
    private Double pfBpm;
    private Double cBpm;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Integer getPgId() { return pgId; }
    public void setPgId(Integer pgId) { this.pgId = pgId; }
    public Integer getSgId() { return sgId; }
    public void setSgId(Integer sgId) { this.sgId = sgId; }
    public Integer getSfId() { return sfId; }
    public void setSfId(Integer sfId) { this.sfId = sfId; }
    public Integer getPfId() { return pfId; }
    public void setPfId(Integer pfId) { this.pfId = pfId; }
    public Integer getCId() { return cId; }
    public void setCId(Integer cId) { this.cId = cId; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public BigDecimal getTotalRebound() { return totalRebound; }
    public void setTotalRebound(BigDecimal totalRebound) { this.totalRebound = totalRebound; }
    public BigDecimal getTotalAssist() { return totalAssist; }
    public void setTotalAssist(BigDecimal totalAssist) { this.totalAssist = totalAssist; }
    public BigDecimal getTotalPer() { return totalPer; }
    public void setTotalPer(BigDecimal totalPer) { this.totalPer = totalPer; }
    public BigDecimal getTotalWs() { return totalWs; }
    public void setTotalWs(BigDecimal totalWs) { this.totalWs = totalWs; }
    public BigDecimal getTotalBpm() { return totalBpm; }
    public void setTotalBpm(BigDecimal totalBpm) { this.totalBpm = totalBpm; }
    public BigDecimal getLineupRating() { return lineupRating; }
    public void setLineupRating(BigDecimal lineupRating) { this.lineupRating = lineupRating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getPgName() { return pgName; }
    public void setPgName(String pgName) { this.pgName = pgName; }
    public String getSgName() { return sgName; }
    public void setSgName(String sgName) { this.sgName = sgName; }
    public String getSfName() { return sfName; }
    public void setSfName(String sfName) { this.sfName = sfName; }
    public String getPfName() { return pfName; }
    public void setPfName(String pfName) { this.pfName = pfName; }
    public String getCName() { return cName; }
    public void setCName(String cName) { this.cName = cName; }
    public String getPgTeam() { return pgTeam; }
    public void setPgTeam(String pgTeam) { this.pgTeam = pgTeam; }
    public String getSgTeam() { return sgTeam; }
    public void setSgTeam(String sgTeam) { this.sgTeam = sgTeam; }
    public String getSfTeam() { return sfTeam; }
    public void setSfTeam(String sfTeam) { this.sfTeam = sfTeam; }
    public String getPfTeam() { return pfTeam; }
    public void setPfTeam(String pfTeam) { this.pfTeam = pfTeam; }
    public String getCTeam() { return cTeam; }
    public void setCTeam(String cTeam) { this.cTeam = cTeam; }
    public String getPgImage() { return pgImage; }
    public void setPgImage(String pgImage) { this.pgImage = pgImage; }
    public String getSgImage() { return sgImage; }
    public void setSgImage(String sgImage) { this.sgImage = sgImage; }
    public String getSfImage() { return sfImage; }
    public void setSfImage(String sfImage) { this.sfImage = sfImage; }
    public String getPfImage() { return pfImage; }
    public void setPfImage(String pfImage) { this.pfImage = pfImage; }
    public String getCImage() { return cImage; }
    public void setCImage(String cImage) { this.cImage = cImage; }
    public Double getPgScore() { return pgScore; }
    public void setPgScore(Double pgScore) { this.pgScore = pgScore; }
    public Double getSgScore() { return sgScore; }
    public void setSgScore(Double sgScore) { this.sgScore = sgScore; }
    public Double getSfScore() { return sfScore; }
    public void setSfScore(Double sfScore) { this.sfScore = sfScore; }
    public Double getPfScore() { return pfScore; }
    public void setPfScore(Double pfScore) { this.pfScore = pfScore; }
    public Double getCScore() { return cScore; }
    public void setCScore(Double cScore) { this.cScore = cScore; }
    public Double getPgRebound() { return pgRebound; }
    public void setPgRebound(Double pgRebound) { this.pgRebound = pgRebound; }
    public Double getSgRebound() { return sgRebound; }
    public void setSgRebound(Double sgRebound) { this.sgRebound = sgRebound; }
    public Double getSfRebound() { return sfRebound; }
    public void setSfRebound(Double sfRebound) { this.sfRebound = sfRebound; }
    public Double getPfRebound() { return pfRebound; }
    public void setPfRebound(Double pfRebound) { this.pfRebound = pfRebound; }
    public Double getCRebound() { return cRebound; }
    public void setCRebound(Double cRebound) { this.cRebound = cRebound; }
    public Double getPgAssist() { return pgAssist; }
    public void setPgAssist(Double pgAssist) { this.pgAssist = pgAssist; }
    public Double getSgAssist() { return sgAssist; }
    public void setSgAssist(Double sgAssist) { this.sgAssist = sgAssist; }
    public Double getSfAssist() { return sfAssist; }
    public void setSfAssist(Double sfAssist) { this.sfAssist = sfAssist; }
    public Double getPfAssist() { return pfAssist; }
    public void setPfAssist(Double pfAssist) { this.pfAssist = pfAssist; }
    public Double getCAssist() { return cAssist; }
    public void setCAssist(Double cAssist) { this.cAssist = cAssist; }
    public Double getPgPer() { return pgPer; }
    public void setPgPer(Double pgPer) { this.pgPer = pgPer; }
    public Double getSgPer() { return sgPer; }
    public void setSgPer(Double sgPer) { this.sgPer = sgPer; }
    public Double getSfPer() { return sfPer; }
    public void setSfPer(Double sfPer) { this.sfPer = sfPer; }
    public Double getPfPer() { return pfPer; }
    public void setPfPer(Double pfPer) { this.pfPer = pfPer; }
    public Double getCPer() { return cPer; }
    public void setCPer(Double cPer) { this.cPer = cPer; }
    public Double getPgWs() { return pgWs; }
    public void setPgWs(Double pgWs) { this.pgWs = pgWs; }
    public Double getSgWs() { return sgWs; }
    public void setSgWs(Double sgWs) { this.sgWs = sgWs; }
    public Double getSfWs() { return sfWs; }
    public void setSfWs(Double sfWs) { this.sfWs = sfWs; }
    public Double getPfWs() { return pfWs; }
    public void setPfWs(Double pfWs) { this.pfWs = pfWs; }
    public Double getCWs() { return cWs; }
    public void setCWs(Double cWs) { this.cWs = cWs; }
    public Double getPgBpm() { return pgBpm; }
    public void setPgBpm(Double pgBpm) { this.pgBpm = pgBpm; }
    public Double getSgBpm() { return sgBpm; }
    public void setSgBpm(Double sgBpm) { this.sgBpm = sgBpm; }
    public Double getSfBpm() { return sfBpm; }
    public void setSfBpm(Double sfBpm) { this.sfBpm = sfBpm; }
    public Double getPfBpm() { return pfBpm; }
    public void setPfBpm(Double pfBpm) { this.pfBpm = pfBpm; }
    public Double getCBpm() { return cBpm; }
    public void setCBpm(Double cBpm) { this.cBpm = cBpm; }
}
