package com.nba.demo.controller;

import com.nba.demo.common.Result;
import com.nba.demo.entity.Emp;
import com.nba.demo.entity.UserStats;
import com.nba.demo.mapper.EmpMapper;
import com.nba.demo.mapper.UserStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private EmpMapper empMapper;
    
    @Autowired
    private UserStatsMapper userStatsMapper;
    
    private static final Map<String, Emp> tokenStore = new HashMap<>();
    
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public Result<?> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        Emp emp = empMapper.findByUsername(username.trim());
        if (emp == null) {
            return Result.error("用户不存在");
        }
        
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, emp);
        
        UserStats stats = userStatsMapper.findByUsername(username.trim());
        if (stats == null) {
            stats = new UserStats();
            stats.setUsername(username.trim());
            userStatsMapper.insert(stats);
        }
        userStatsMapper.updateLastLoginDate(username.trim(), LocalDate.now());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", emp.getId());
        data.put("username", emp.getUsername());
        data.put("name", emp.getName());
        
        return Result.success(data);
    }
    
    @PostMapping(value = "/register", produces = "application/json;charset=UTF-8")
    public Result<?> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String name = params.get("name");
        
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            return Result.error("密码至少6位");
        }
        if (username.length() < 3 || username.length() > 20) {
            return Result.error("用户名长度3-20位");
        }
        
        int count = empMapper.countByUsername(username.trim());
        if (count > 0) {
            return Result.error("用户名已存在");
        }
        
        Emp emp = new Emp();
        emp.setUsername(username.trim());
        emp.setPassword(password);
        emp.setName(name != null && !name.trim().isEmpty() ? name.trim() : username.trim());
        emp.setGender(1);
        
        int result = empMapper.insert(emp);
        if (result > 0) {
            UserStats stats = new UserStats();
            stats.setUsername(username.trim());
            userStatsMapper.insert(stats);
            
            return Result.success("注册成功");
        }
        
        return Result.error("注册失败");
    }
    
    @GetMapping(value = "/user/info", produces = "application/json;charset=UTF-8")
    public Result<?> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        token = token.replace("Bearer ", "");
        Emp emp = tokenStore.get(token);
        
        if (emp == null) {
            return Result.error("登录已过期，请重新登录");
        }
        
        Emp latestEmp = empMapper.findById(emp.getId());
        if (latestEmp == null) {
            return Result.error("用户不存在");
        }
        
        UserStats stats = userStatsMapper.findByUsername(latestEmp.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", latestEmp.getId());
        data.put("username", latestEmp.getUsername());
        data.put("name", latestEmp.getName());
        data.put("gender", latestEmp.getGender());
        data.put("phone", latestEmp.getPhone());
        data.put("image", latestEmp.getImage());
        data.put("createTime", latestEmp.getCreateTime());
        data.put("updateTime", latestEmp.getUpdateTime());
        
        if (stats != null) {
            data.put("viewCount", stats.getViewCount());
            data.put("favoriteCount", stats.getFavoriteCount());
            data.put("predictionCount", stats.getPredictionCount());
            data.put("lastLoginDate", stats.getLastLoginDate());
            data.put("favoriteTeam", stats.getFavoriteTeam());
            data.put("favoritePlayer", stats.getFavoritePlayer());
        } else {
            data.put("viewCount", 0);
            data.put("favoriteCount", 0);
            data.put("predictionCount", 0);
            data.put("lastLoginDate", null);
            data.put("favoriteTeam", null);
            data.put("favoritePlayer", null);
        }
        
        return Result.success(data);
    }
    
    @PostMapping(value = "/user/update", produces = "application/json;charset=UTF-8")
    public Result<?> updateUserInfo(@RequestHeader(value = "Authorization", required = false) String token,
                                     @RequestBody Map<String, Object> params) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        token = token.replace("Bearer ", "");
        Emp emp = tokenStore.get(token);
        
        if (emp == null) {
            return Result.error("登录已过期，请重新登录");
        }
        
        Emp updateEmp = new Emp();
        updateEmp.setId(emp.getId());
        updateEmp.setName((String) params.get("name"));
        
        if (params.get("gender") != null) {
            updateEmp.setGender(((Number) params.get("gender")).intValue());
        }
        updateEmp.setPhone((String) params.get("phone"));
        updateEmp.setImage((String) params.get("image"));
        
        int result = empMapper.update(updateEmp);
        if (result > 0) {
            return Result.success("更新成功");
        }
        
        return Result.error("更新失败");
    }
    
    @PostMapping(value = "/user/favorites", produces = "application/json;charset=UTF-8")
    public Result<?> updateFavorites(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestBody Map<String, String> params) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        token = token.replace("Bearer ", "");
        Emp emp = tokenStore.get(token);
        
        if (emp == null) {
            return Result.error("登录已过期，请重新登录");
        }
        
        String favoriteTeam = params.get("favoriteTeam");
        String favoritePlayer = params.get("favoritePlayer");
        
        int result = userStatsMapper.updateFavorites(emp.getUsername(), favoriteTeam, favoritePlayer);
        if (result > 0) {
            return Result.success("更新成功");
        }
        
        return Result.error("更新失败");
    }
    
    @GetMapping(value = "/user/favorites", produces = "application/json;charset=UTF-8")
    public Result<?> getFavorites(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        token = token.replace("Bearer ", "");
        Emp emp = tokenStore.get(token);
        
        if (emp == null) {
            return Result.error("登录已过期");
        }
        
        UserStats stats = userStatsMapper.findByUsername(emp.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        if (stats != null) {
            data.put("favoriteTeam", stats.getFavoriteTeam());
            data.put("favoritePlayer", stats.getFavoritePlayer());
        } else {
            data.put("favoriteTeam", null);
            data.put("favoritePlayer", null);
        }
        
        return Result.success(data);
    }
    
    @PostMapping(value = "/user/password", produces = "application/json;charset=UTF-8")
    public Result<?> updatePassword(@RequestHeader(value = "Authorization", required = false) String token,
                                     @RequestBody Map<String, String> params) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        token = token.replace("Bearer ", "");
        Emp emp = tokenStore.get(token);
        
        if (emp == null) {
            return Result.error("登录已过期，请重新登录");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("参数错误");
        }
        
        Emp latestEmp = empMapper.findById(emp.getId());
        if (!latestEmp.getPassword().equals(oldPassword)) {
            return Result.error("原密码错误");
        }
        
        if (newPassword.length() < 6) {
            return Result.error("新密码至少6位");
        }
        
        int result = empMapper.updatePassword(emp.getId(), newPassword);
        if (result > 0) {
            return Result.success("密码修改成功");
        }
        
        return Result.error("密码修改失败");
    }
    
    @PostMapping(value = "/user/stats/increment", produces = "application/json;charset=UTF-8")
    public Result<?> incrementStats(@RequestHeader(value = "Authorization", required = false) String token,
                                     @RequestBody Map<String, String> params) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        token = token.replace("Bearer ", "");
        Emp emp = tokenStore.get(token);
        
        if (emp == null) {
            return Result.error("登录已过期");
        }
        
        String type = params.get("type");
        int result = 0;
        
        switch (type) {
            case "view":
                result = userStatsMapper.incrementViewCount(emp.getUsername());
                break;
            case "favorite":
                result = userStatsMapper.incrementFavoriteCount(emp.getUsername());
                break;
            case "prediction":
                result = userStatsMapper.incrementPredictionCount(emp.getUsername());
                break;
            default:
                return Result.error("无效的统计类型");
        }
        
        if (result > 0) {
            return Result.success("统计更新成功");
        }
        
        return Result.error("统计更新失败");
    }
    
    @PostMapping(value = "/logout", produces = "application/json;charset=UTF-8")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            token = token.replace("Bearer ", "");
            tokenStore.remove(token);
        }
        return Result.success("退出成功");
    }
    
    public static Emp getEmpByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return tokenStore.get(token.replace("Bearer ", ""));
    }
}
