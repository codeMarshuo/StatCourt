package com.nba.demo.controller;

import com.nba.demo.common.Result;
import com.nba.demo.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class DataController {
    
    @Autowired
    private DataService dataService;
    
    @GetMapping(value = "/data", produces = "application/json;charset=UTF-8")
    public Result<Map<String, Object>> getAllData() {
        try {
            Map<String, Object> data = dataService.getAllData();
            return Result.success("获取数据成功", data);
        } catch (Exception e) {
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/players", produces = "application/json;charset=UTF-8")
    public Result<?> getAllPlayers() {
        try {
            return Result.success(dataService.getAllPlayers());
        } catch (Exception e) {
            return Result.error("获取球员数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/teams", produces = "application/json;charset=UTF-8")
    public Result<?> getAllTeams() {
        try {
            return Result.success(dataService.getAllTeams());
        } catch (Exception e) {
            return Result.error("获取球队数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/team/{teamid}", produces = "application/json;charset=UTF-8")
    public Result<?> getTeamById(@PathVariable Integer teamid) {
        try {
            return Result.success(dataService.getTeamById(teamid));
        } catch (Exception e) {
            return Result.error("获取球队数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/team/top3/{teamName}", produces = "application/json;charset=UTF-8")
    public Result<?> getTop3PlayersByTeam(@PathVariable String teamName) {
        try {
            return Result.success(dataService.getTop3PlayersByTeam(teamName));
        } catch (Exception e) {
            return Result.error("获取球员数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/team/detail/{teamName}", produces = "application/json;charset=UTF-8")
    public Result<?> getTeamDetailByName(@PathVariable String teamName) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("team", dataService.getTeamByName(teamName));
            result.put("topPlayers", dataService.getTop3PlayersByTeam(teamName));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取球队详情失败: " + e.getMessage());
        }
    }
}
