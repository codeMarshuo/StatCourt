package com.nba.demo.service;

import com.nba.demo.entity.Player;
import com.nba.demo.entity.Team;
import com.nba.demo.mapper.PlayerMapper;
import com.nba.demo.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {
    
    @Autowired
    private PlayerMapper playerMapper;
    
    @Autowired
    private TeamMapper teamMapper;
    
    public Map<String, Object> getAllData() {
        Map<String, Object> data = new HashMap<>();
        List<Player> players = playerMapper.findAll();
        List<Team> teams = teamMapper.findAll();
        data.put("players", players);
        data.put("teams", teams);
        return data;
    }
    
    public List<Player> getAllPlayers() {
        return playerMapper.findAll();
    }
    
    public List<Team> getAllTeams() {
        return teamMapper.findAll();
    }
    
    public Player getPlayerById(Integer id) {
        return playerMapper.findById(id);
    }
    
    public Team getTeamById(Integer teamid) {
        return teamMapper.findById(teamid);
    }
    
    public List<Player> getTop3PlayersByTeam(String team) {
        return playerMapper.findTop3ByTeam(team);
    }
    
    public Team getTeamByName(String teamName) {
        List<Team> teams = teamMapper.findAll();
        for (Team team : teams) {
            if (team.getTeam() != null && team.getTeam().contains(teamName)) {
                return team;
            }
        }
        return null;
    }
}
