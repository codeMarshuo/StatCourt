package com.nba.demo.controller;

import com.nba.demo.common.Result;
import com.nba.demo.entity.Emp;
import com.nba.demo.entity.Player;
import com.nba.demo.entity.UserTeam;
import com.nba.demo.mapper.PlayerMapper;
import com.nba.demo.service.UserTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
@CrossOrigin
public class UserTeamController {

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private PlayerMapper playerMapper;

    @PostMapping(value = "/create", produces = "application/json;charset=UTF-8")
    public Result<?> createTeam(@RequestHeader(value = "Authorization", required = false) String token,
                                 @RequestBody UserTeam team) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        if (team.getTeamName() == null || team.getTeamName().trim().isEmpty()) {
            return Result.error("阵容名称不能为空");
        }

        team.setUserId(emp.getId());
        UserTeam created = userTeamService.createTeam(team);
        return Result.success("阵容创建成功", created);
    }

    @PutMapping(value = "/update/{id}", produces = "application/json;charset=UTF-8")
    public Result<?> updateTeam(@RequestHeader(value = "Authorization", required = false) String token,
                                 @PathVariable Integer id,
                                 @RequestBody UserTeam team) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        UserTeam existing = userTeamService.getTeamById(id);
        if (existing == null) {
            return Result.error("阵容不存在");
        }

        if (!existing.getUserId().equals(emp.getId())) {
            return Result.error("无权修改此阵容");
        }

        team.setId(id);
        team.setUserId(emp.getId());
        UserTeam updated = userTeamService.updateTeam(team);
        return Result.success("阵容更新成功", updated);
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json;charset=UTF-8")
    public Result<?> deleteTeam(@RequestHeader(value = "Authorization", required = false) String token,
                                 @PathVariable Integer id) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        UserTeam existing = userTeamService.getTeamById(id);
        if (existing == null) {
            return Result.error("阵容不存在");
        }

        if (!existing.getUserId().equals(emp.getId())) {
            return Result.error("无权删除此阵容");
        }

        userTeamService.deleteTeam(id);
        return Result.success("阵容删除成功");
    }

    @GetMapping(value = "/my", produces = "application/json;charset=UTF-8")
    public Result<?> getMyTeams(@RequestHeader(value = "Authorization", required = false) String token) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        List<UserTeam> teams = userTeamService.getTeamsByUserId(emp.getId());
        return Result.success(teams);
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Result<?> getTeamById(@PathVariable Integer id) {
        UserTeam team = userTeamService.getTeamById(id);
        if (team == null) {
            return Result.error("阵容不存在");
        }
        return Result.success(team);
    }

    @GetMapping(value = "/all", produces = "application/json;charset=UTF-8")
    public Result<?> getAllTeams() {
        List<UserTeam> teams = userTeamService.getAllTeams();
        return Result.success(teams);
    }

    @GetMapping(value = "/compare/{id1}/{id2}", produces = "application/json;charset=UTF-8")
    public Result<?> compareTeams(@PathVariable Integer id1, @PathVariable Integer id2) {
        Map<String, Object> comparison = userTeamService.compareTeams(id1, id2);
        if (comparison == null) {
            return Result.error("阵容不存在");
        }
        return Result.success(comparison);
    }

    @GetMapping(value = "/players", produces = "application/json;charset=UTF-8")
    public Result<?> getAllPlayers() {
        List<Player> players = playerMapper.findAll();
        return Result.success(players);
    }
}
