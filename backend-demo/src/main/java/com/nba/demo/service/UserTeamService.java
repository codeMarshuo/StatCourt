package com.nba.demo.service;

import com.nba.demo.entity.NbaAdvancedStats;
import com.nba.demo.entity.Player;
import com.nba.demo.entity.UserTeam;
import com.nba.demo.mapper.NbaAdvancedStatsMapper;
import com.nba.demo.mapper.PlayerMapper;
import com.nba.demo.mapper.UserTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserTeamService {

    @Autowired
    private UserTeamMapper userTeamMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private NbaAdvancedStatsMapper advancedStatsMapper;

    public UserTeam createTeam(UserTeam team) {
        calculateTeamStats(team);
        userTeamMapper.insert(team);
        return team;
    }

    public UserTeam updateTeam(UserTeam team) {
        calculateTeamStats(team);
        userTeamMapper.update(team);
        return getTeamWithPlayerInfo(team.getId());
    }

    public void deleteTeam(Integer id) {
        userTeamMapper.deleteById(id);
    }

    public List<UserTeam> getTeamsByUserId(Integer userId) {
        List<UserTeam> teams = userTeamMapper.selectByUserId(userId);
        for (UserTeam team : teams) {
            fillPlayerInfo(team);
        }
        return teams;
    }

    public UserTeam getTeamById(Integer id) {
        UserTeam team = userTeamMapper.selectById(id);
        if (team != null) {
            fillPlayerInfo(team);
        }
        return team;
    }

    public UserTeam getTeamWithPlayerInfo(Integer id) {
        return getTeamById(id);
    }

    public List<UserTeam> getAllTeams() {
        List<UserTeam> teams = userTeamMapper.selectAll();
        for (UserTeam team : teams) {
            fillPlayerInfo(team);
        }
        return teams;
    }

    private void calculateTeamStats(UserTeam team) {
        double totalScore = 0;
        double totalRebound = 0;
        double totalAssist = 0;
        double totalPer = 0;
        double totalWs = 0;
        double totalBpm = 0;
        int playerCount = 0;

        Integer[] playerIds = {team.getPgId(), team.getSgId(), team.getSfId(), team.getPfId(), team.getCId()};
        
        for (Integer playerId : playerIds) {
            if (playerId != null) {
                Player player = playerMapper.findById(playerId);
                
                if (player != null) {
                    totalScore += player.getScore() != null ? player.getScore() : 0;
                    totalRebound += player.getRebound() != null ? player.getRebound() : 0;
                    totalAssist += player.getAssist() != null ? player.getAssist() : 0;
                    playerCount++;
                }
            }
        }

        team.setTotalScore(BigDecimal.valueOf(totalScore));
        team.setTotalRebound(BigDecimal.valueOf(totalRebound));
        team.setTotalAssist(BigDecimal.valueOf(totalAssist));
        team.setTotalPer(BigDecimal.valueOf(totalPer));
        team.setTotalWs(BigDecimal.valueOf(totalWs));
        team.setTotalBpm(BigDecimal.valueOf(totalBpm));

        double avgScore = playerCount > 0 ? totalScore / playerCount : 0;
        double avgRebound = playerCount > 0 ? totalRebound / playerCount : 0;
        double avgAssist = playerCount > 0 ? totalAssist / playerCount : 0;
        
        double rating = (avgScore * 3 + avgRebound * 2 + avgAssist * 2) / 7;
        rating = Math.max(0, Math.min(100, rating));
        
        team.setLineupRating(BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP));
    }

    private void fillPlayerInfo(UserTeam team) {
        fillSinglePlayerInfo(team, team.getPgId(), "pg");
        fillSinglePlayerInfo(team, team.getSgId(), "sg");
        fillSinglePlayerInfo(team, team.getSfId(), "sf");
        fillSinglePlayerInfo(team, team.getPfId(), "pf");
        fillSinglePlayerInfo(team, team.getCId(), "c");
    }

    private void fillSinglePlayerInfo(UserTeam team, Integer playerId, String position) {
        if (playerId == null) return;
        
        Player player = playerMapper.findById(playerId);
        if (player == null) return;
        
        switch (position) {
            case "pg":
                team.setPgName(player.getPlayerName());
                team.setPgTeam(player.getTeam());
                team.setPgImage(player.getPlayerImage());
                team.setPgScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                team.setPgRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                team.setPgAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                team.setPgPer(0.0);
                team.setPgWs(0.0);
                team.setPgBpm(0.0);
                break;
            case "sg":
                team.setSgName(player.getPlayerName());
                team.setSgTeam(player.getTeam());
                team.setSgImage(player.getPlayerImage());
                team.setSgScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                team.setSgRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                team.setSgAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                team.setSgPer(0.0);
                team.setSgWs(0.0);
                team.setSgBpm(0.0);
                break;
            case "sf":
                team.setSfName(player.getPlayerName());
                team.setSfTeam(player.getTeam());
                team.setSfImage(player.getPlayerImage());
                team.setSfScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                team.setSfRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                team.setSfAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                team.setSfPer(0.0);
                team.setSfWs(0.0);
                team.setSfBpm(0.0);
                break;
            case "pf":
                team.setPfName(player.getPlayerName());
                team.setPfTeam(player.getTeam());
                team.setPfImage(player.getPlayerImage());
                team.setPfScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                team.setPfRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                team.setPfAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                team.setPfPer(0.0);
                team.setPfWs(0.0);
                team.setPfBpm(0.0);
                break;
            case "c":
                team.setCName(player.getPlayerName());
                team.setCTeam(player.getTeam());
                team.setCImage(player.getPlayerImage());
                team.setCScore(player.getScore() != null ? player.getScore().doubleValue() : 0.0);
                team.setCRebound(player.getRebound() != null ? player.getRebound().doubleValue() : 0.0);
                team.setCAssist(player.getAssist() != null ? player.getAssist().doubleValue() : 0.0);
                team.setCPer(0.0);
                team.setCWs(0.0);
                team.setCBpm(0.0);
                break;
        }
    }

    public Map<String, Object> compareTeams(Integer team1Id, Integer team2Id) {
        UserTeam team1 = getTeamById(team1Id);
        UserTeam team2 = getTeamById(team2Id);

        if (team1 == null || team2 == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("team1", team1);
        result.put("team2", team2);

        Map<String, Object> comparison = new HashMap<>();
        
        double score1 = team1.getTotalScore() != null ? team1.getTotalScore().doubleValue() : 0;
        double score2 = team2.getTotalScore() != null ? team2.getTotalScore().doubleValue() : 0;
        comparison.put("scoreDiff", score1 - score2);
        comparison.put("scoreWinner", score1 > score2 ? 1 : (score1 < score2 ? 2 : 0));

        double rebound1 = team1.getTotalRebound() != null ? team1.getTotalRebound().doubleValue() : 0;
        double rebound2 = team2.getTotalRebound() != null ? team2.getTotalRebound().doubleValue() : 0;
        comparison.put("reboundDiff", rebound1 - rebound2);
        comparison.put("reboundWinner", rebound1 > rebound2 ? 1 : (rebound1 < rebound2 ? 2 : 0));

        double assist1 = team1.getTotalAssist() != null ? team1.getTotalAssist().doubleValue() : 0;
        double assist2 = team2.getTotalAssist() != null ? team2.getTotalAssist().doubleValue() : 0;
        comparison.put("assistDiff", assist1 - assist2);
        comparison.put("assistWinner", assist1 > assist2 ? 1 : (assist1 < assist2 ? 2 : 0));

        double per1 = team1.getTotalPer() != null ? team1.getTotalPer().doubleValue() : 0;
        double per2 = team2.getTotalPer() != null ? team2.getTotalPer().doubleValue() : 0;
        comparison.put("perDiff", per1 - per2);
        comparison.put("perWinner", per1 > per2 ? 1 : (per1 < per2 ? 2 : 0));

        double ws1 = team1.getTotalWs() != null ? team1.getTotalWs().doubleValue() : 0;
        double ws2 = team2.getTotalWs() != null ? team2.getTotalWs().doubleValue() : 0;
        comparison.put("wsDiff", ws1 - ws2);
        comparison.put("wsWinner", ws1 > ws2 ? 1 : (ws1 < ws2 ? 2 : 0));

        double bpm1 = team1.getTotalBpm() != null ? team1.getTotalBpm().doubleValue() : 0;
        double bpm2 = team2.getTotalBpm() != null ? team2.getTotalBpm().doubleValue() : 0;
        comparison.put("bpmDiff", bpm1 - bpm2);
        comparison.put("bpmWinner", bpm1 > bpm2 ? 1 : (bpm1 < bpm2 ? 2 : 0));

        double rating1 = team1.getLineupRating() != null ? team1.getLineupRating().doubleValue() : 0;
        double rating2 = team2.getLineupRating() != null ? team2.getLineupRating().doubleValue() : 0;
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
        comparison.put("team1Wins", wins1);
        comparison.put("team2Wins", wins2);

        result.put("comparison", comparison);

        return result;
    }
}
