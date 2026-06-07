package com.nba.demo.controller;

import com.nba.demo.common.Result;
import com.nba.demo.entity.Emp;
import com.nba.demo.entity.Player;
import com.nba.demo.entity.UserLineup;
import com.nba.demo.mapper.PlayerMapper;
import com.nba.demo.service.UserLineupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lineup")
@CrossOrigin
public class UserLineupController {

    @Autowired
    private UserLineupService userLineupService;

    @Autowired
    private PlayerMapper playerMapper;

    @PostMapping(value = "/create", produces = "application/json;charset=UTF-8")
    public Result<?> createLineup(@RequestHeader(value = "Authorization", required = false) String token,
                                   @RequestBody UserLineup lineup) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        if (lineup.getLineupName() == null || lineup.getLineupName().trim().isEmpty()) {
            return Result.error("阵容名称不能为空");
        }

        lineup.setUserId(emp.getId());
        UserLineup created = userLineupService.createLineup(lineup);
        return Result.success("阵容创建成功", created);
    }

    @PutMapping(value = "/update/{id}", produces = "application/json;charset=UTF-8")
    public Result<?> updateLineup(@RequestHeader(value = "Authorization", required = false) String token,
                                   @PathVariable Integer id,
                                   @RequestBody UserLineup lineup) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        UserLineup existing = userLineupService.getLineupById(id);
        if (existing == null) {
            return Result.error("阵容不存在");
        }

        if (!existing.getUserId().equals(emp.getId())) {
            return Result.error("无权修改此阵容");
        }

        lineup.setId(id);
        lineup.setUserId(emp.getId());
        UserLineup updated = userLineupService.updateLineup(lineup);
        return Result.success("阵容更新成功", updated);
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json;charset=UTF-8")
    public Result<?> deleteLineup(@RequestHeader(value = "Authorization", required = false) String token,
                                   @PathVariable Integer id) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        UserLineup existing = userLineupService.getLineupById(id);
        if (existing == null) {
            return Result.error("阵容不存在");
        }

        if (!existing.getUserId().equals(emp.getId())) {
            return Result.error("无权删除此阵容");
        }

        userLineupService.deleteLineup(id);
        return Result.success("阵容删除成功");
    }

    @GetMapping(value = "/my", produces = "application/json;charset=UTF-8")
    public Result<?> getMyLineups(@RequestHeader(value = "Authorization", required = false) String token) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("请先登录");
        }

        List<UserLineup> lineups = userLineupService.getLineupsByUserId(emp.getId());
        return Result.success(lineups);
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Result<?> getLineupById(@PathVariable Integer id) {
        UserLineup lineup = userLineupService.getLineupById(id);
        if (lineup == null) {
            return Result.error("阵容不存在");
        }
        return Result.success(lineup);
    }

    @GetMapping(value = "/all", produces = "application/json;charset=UTF-8")
    public Result<?> getAllLineups() {
        List<UserLineup> lineups = userLineupService.getAllLineups();
        return Result.success(lineups);
    }

    @GetMapping(value = "/compare/{id1}/{id2}", produces = "application/json;charset=UTF-8")
    public Result<?> compareLineups(@PathVariable Integer id1, @PathVariable Integer id2) {
        Map<String, Object> comparison = userLineupService.compareLineups(id1, id2);
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

    @GetMapping(value = "/players/top", produces = "application/json;charset=UTF-8")
    public Result<?> getTopPlayers(@RequestParam(defaultValue = "50") int limit) {
        List<Player> players = playerMapper.findAll();
        if (players.size() > limit) {
            players = players.subList(0, limit);
        }
        return Result.success(players);
    }
}
