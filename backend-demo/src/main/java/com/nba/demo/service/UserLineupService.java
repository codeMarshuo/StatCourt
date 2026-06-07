package com.nba.demo.service;

import com.nba.demo.entity.AdvancedStats;
import com.nba.demo.entity.Player;
import com.nba.demo.entity.UserLineup;
import com.nba.demo.mapper.AdvancedStatsMapper;
import com.nba.demo.mapper.PlayerMapper;
import com.nba.demo.mapper.UserLineupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLineupService {

    @Autowired
    private UserLineupMapper userLineupMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private AdvancedStatsMapper advancedStatsMapper;

    public UserLineup createLineup(UserLineup lineup) {
        calculateLineupStats(lineup);
        userLineupMapper.insert(lineup);
        return lineup;
    }

    public UserLineup updateLineup(UserLineup lineup) {
        calculateLineupStats(lineup);
        userLineupMapper.update(lineup);
        return getLineupWithPlayerInfo(lineup.getId());
    }

    public void deleteLineup(Integer id) {
        userLineupMapper.deleteById(id);
    }

    public List<UserLineup> getLineupsByUserId(Integer userId) {
        List<UserLineup> lineups = userLineupMapper.selectByUserId(userId);
        for (UserLineup lineup : lineups) {
            fillPlayerInfo(lineup);
        }
        return lineups;
    }

    public UserLineup getLineupById(Integer id) {
        UserLineup lineup = userLineupMapper.selectById(id);
        if (lineup != null) {
            fillPlayerInfo(lineup);
        }
        return lineup;
    }

    public UserLineup getLineupWithPlayerInfo(Integer id) {
        return getLineupById(id);
    }

    public List<UserLineup> getAllLineups() {
        List<UserLineup> lineups = userLineupMapper.selectAll();
        for (UserLineup lineup : lineups) {
            fillPlayerInfo(lineup);
        }
        return lineups;
    }

    private void calculateLineupStats(UserLineup lineup) {
        double totalScore = 0;
        double totalRebound = 0;
        double totalAssist = 0;
        double totalPer = 0;
        double totalWs = 0;
        double totalBpm = 0;
        int playerCount = 0;

        Integer[] playerIds = {lineup.getPgId(), lineup.getSgId(), lineup.getSfId(), lineup.getPfId(), lineup.getCId()};
        
        for (Integer playerId : playerIds) {
            if (playerId != null) {
                Player player = playerMapper.findById(playerId);
                AdvancedStats advancedStats = advancedStatsMapper.findById(playerId);
                
                if (player != null) {
                    totalScore += player.getScore() != null ? player.getScore() : 0;
                    totalRebound += player.getRebound() != null ? player.getRebound() : 0;
                    totalAssist += player.getAssist() != null ? player.getAssist() : 0;
                }
                
                if (advancedStats != null) {
                    totalPer += advancedStats.getPer() != null ? advancedStats.getPer().doubleValue() : 0;
                    totalWs += advancedStats.getWs() != null ? advancedStats.getWs().doubleValue() : 0;
                    totalBpm += advancedStats.getBpm() != null ? advancedStats.getBpm().doubleValue() : 0;
                }
                
                playerCount++;
            }
        }

        lineup.setTotalScore(BigDecimal.valueOf(totalScore));
        lineup.setTotalRebound(BigDecimal.valueOf(totalRebound));
        lineup.setTotalAssist(BigDecimal.valueOf(totalAssist));
        lineup.setTotalPer(BigDecimal.valueOf(totalPer));
        lineup.setTotalWs(BigDecimal.valueOf(totalWs));
        lineup.setTotalBpm(BigDecimal.valueOf(totalBpm));

        double avgPer = playerCount > 0 ? totalPer / playerCount : 0;
        double avgWs = playerCount > 0 ? totalWs / playerCount : 0;
        double avgBpm = playerCount > 0 ? totalBpm / playerCount : 0;
        double avgScore = playerCount > 0 ? totalScore / playerCount : 0;
        
        double rating = (avgPer * 2 + avgWs * 3 + avgBpm * 1.5 + avgScore * 0.5) / 7 * 10;
        rating = Math.max(0, Math.min(100, rating));
        
        lineup.setLineupRating(BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP));
    }

    private void fillPlayerInfo(UserLineup lineup) {
        fillSinglePlayerInfo(lineup, lineup.getPgId(), "pg");
        fillSinglePlayerInfo(lineup, lineup.getSgId(), "sg");
        fillSinglePlayerInfo(lineup, lineup.getSfId(), "sf");
        fillSinglePlayerInfo(lineup, lineup.getPfId(), "pf");
        fillSinglePlayerInfo(lineup, lineup.getCId(), "c");
    }

    private void fillSinglePlayerInfo(UserLineup lineup, Integer playerId, String position) {
        if (playerId == null) return;
        
        Player player = playerMapper.findById(playerId);
        AdvancedStats advancedStats = advancedStatsMapper.findById(playerId);
        
        if (player == null) return;
        
        switch (position) {
            case "pg":
                lineup.setPgName(player.getPlayerName());
                lineup.setPgTeam(player.getTeam());
                lineup.setPgImage(player.getPlayerImage());
                lineup.setPgScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                lineup.setPgRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                lineup.setPgAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                if (advancedStats != null) {
                    lineup.setPgPer(advancedStats.getPer() != null ? advancedStats.getPer().doubleValue() : 0.0);
                    lineup.setPgWs(advancedStats.getWs() != null ? advancedStats.getWs().doubleValue() : 0.0);
                    lineup.setPgBpm(advancedStats.getBpm() != null ? advancedStats.getBpm().doubleValue() : 0.0);
                }
                break;
            case "sg":
                lineup.setSgName(player.getPlayerName());
                lineup.setSgTeam(player.getTeam());
                lineup.setSgImage(player.getPlayerImage());
                lineup.setSgScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                lineup.setSgRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                lineup.setSgAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                if (advancedStats != null) {
                    lineup.setSgPer(advancedStats.getPer() != null ? advancedStats.getPer().doubleValue() : 0.0);
                    lineup.setSgWs(advancedStats.getWs() != null ? advancedStats.getWs().doubleValue() : 0.0);
                    lineup.setSgBpm(advancedStats.getBpm() != null ? advancedStats.getBpm().doubleValue() : 0.0);
                }
                break;
            case "sf":
                lineup.setSfName(player.getPlayerName());
                lineup.setSfTeam(player.getTeam());
                lineup.setSfImage(player.getPlayerImage());
                lineup.setSfScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                lineup.setSfRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                lineup.setSfAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                if (advancedStats != null) {
                    lineup.setSfPer(advancedStats.getPer() != null ? advancedStats.getPer().doubleValue() : 0.0);
                    lineup.setSfWs(advancedStats.getWs() != null ? advancedStats.getWs().doubleValue() : 0.0);
                    lineup.setSfBpm(advancedStats.getBpm() != null ? advancedStats.getBpm().doubleValue() : 0.0);
                }
                break;
            case "pf":
                lineup.setPfName(player.getPlayerName());
                lineup.setPfTeam(player.getTeam());
                lineup.setPfImage(player.getPlayerImage());
                lineup.setPfScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                lineup.setPfRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                lineup.setPfAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                if (advancedStats != null) {
                    lineup.setPfPer(advancedStats.getPer() != null ? advancedStats.getPer().doubleValue() : 0.0);
                    lineup.setPfWs(advancedStats.getWs() != null ? advancedStats.getWs().doubleValue() : 0.0);
                    lineup.setPfBpm(advancedStats.getBpm() != null ? advancedStats.getBpm().doubleValue() : 0.0);
                }
                break;
            case "c":
                lineup.setCName(player.getPlayerName());
                lineup.setCTeam(player.getTeam());
                lineup.setCImage(player.getPlayerImage());
                lineup.setCScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                lineup.setCRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                lineup.setCAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                if (advancedStats != null) {
                    lineup.setCPer(advancedStats.getPer() != null ? advancedStats.getPer().doubleValue() : 0.0);
                    lineup.setCWs(advancedStats.getWs() != null ? advancedStats.getWs().doubleValue() : 0.0);
                    lineup.setCBpm(advancedStats.getBpm() != null ? advancedStats.getBpm().doubleValue() : 0.0);
                }
                break;
        }
    }

    public Map<String, Object> compareLineups(Integer lineup1Id, Integer lineup2Id) {
        UserLineup lineup1 = getLineupById(lineup1Id);
        UserLineup lineup2 = getLineupById(lineup2Id);

        if (lineup1 == null || lineup2 == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("lineup1", lineup1);
        result.put("lineup2", lineup2);

        Map<String, Object> comparison = new HashMap<>();
        
        double score1 = lineup1.getTotalScore() != null ? lineup1.getTotalScore().doubleValue() : 0;
        double score2 = lineup2.getTotalScore() != null ? lineup2.getTotalScore().doubleValue() : 0;
        comparison.put("scoreDiff", score1 - score2);
        comparison.put("scoreWinner", score1 > score2 ? 1 : (score1 < score2 ? 2 : 0));

        double rebound1 = lineup1.getTotalRebound() != null ? lineup1.getTotalRebound().doubleValue() : 0;
        double rebound2 = lineup2.getTotalRebound() != null ? lineup2.getTotalRebound().doubleValue() : 0;
        comparison.put("reboundDiff", rebound1 - rebound2);
        comparison.put("reboundWinner", rebound1 > rebound2 ? 1 : (rebound1 < rebound2 ? 2 : 0));

        double assist1 = lineup1.getTotalAssist() != null ? lineup1.getTotalAssist().doubleValue() : 0;
        double assist2 = lineup2.getTotalAssist() != null ? lineup2.getTotalAssist().doubleValue() : 0;
        comparison.put("assistDiff", assist1 - assist2);
        comparison.put("assistWinner", assist1 > assist2 ? 1 : (assist1 < assist2 ? 2 : 0));

        double per1 = lineup1.getTotalPer() != null ? lineup1.getTotalPer().doubleValue() : 0;
        double per2 = lineup2.getTotalPer() != null ? lineup2.getTotalPer().doubleValue() : 0;
        comparison.put("perDiff", per1 - per2);
        comparison.put("perWinner", per1 > per2 ? 1 : (per1 < per2 ? 2 : 0));

        double ws1 = lineup1.getTotalWs() != null ? lineup1.getTotalWs().doubleValue() : 0;
        double ws2 = lineup2.getTotalWs() != null ? lineup2.getTotalWs().doubleValue() : 0;
        comparison.put("wsDiff", ws1 - ws2);
        comparison.put("wsWinner", ws1 > ws2 ? 1 : (ws1 < ws2 ? 2 : 0));

        double bpm1 = lineup1.getTotalBpm() != null ? lineup1.getTotalBpm().doubleValue() : 0;
        double bpm2 = lineup2.getTotalBpm() != null ? lineup2.getTotalBpm().doubleValue() : 0;
        comparison.put("bpmDiff", bpm1 - bpm2);
        comparison.put("bpmWinner", bpm1 > bpm2 ? 1 : (bpm1 < bpm2 ? 2 : 0));

        double rating1 = lineup1.getLineupRating() != null ? lineup1.getLineupRating().doubleValue() : 0;
        double rating2 = lineup2.getLineupRating() != null ? lineup2.getLineupRating().doubleValue() : 0;
        comparison.put("ratingDiff", rating1 - rating2);
        comparison.put("ratingWinner", rating1 > rating2 ? 1 : (rating1 < rating2 ? 2 : 0));

        int wins1 = 0, wins2 = 0;
        String[] winnerKeys = {"scoreWinner", "reboundWinner", "assistWinner", "perWinner", "wsWinner", "bpmWinner", "ratingWinner"};
        for (String key : winnerKeys) {
            int winner = (Integer) comparison.get(key);
            if (winner == 1) wins1++;
            else if (winner == 2) wins2++;
        }

        comparison.put("overallWinner", wins1 > wins2 ? 1 : (wins1 < wins2 ? 2 : 0));
        comparison.put("lineup1Wins", wins1);
        comparison.put("lineup2Wins", wins2);

        result.put("comparison", comparison);

        return result;
    }
}
