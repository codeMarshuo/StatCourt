package com.nba.demo.controller;

import com.nba.demo.common.Result;
import com.nba.demo.entity.Emp;
import com.nba.demo.mapper.AdminMapper;
import com.nba.demo.mapper.EmpMapper;
import com.nba.demo.mapper.UserStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private UserStatsMapper userStatsMapper;

    private boolean isAdmin(String token) {
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) return false;
        String role = adminMapper.findRoleByUsername(emp.getUsername());
        return "admin".equals(role);
    }

    @GetMapping("/check")
    public Result<?> checkAdmin(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        token = token.replace("Bearer ", "");
        Emp emp = AuthController.getEmpByToken(token);
        if (emp == null) {
            return Result.error("登录已过期");
        }
        String role = adminMapper.findRoleByUsername(emp.getUsername());
        boolean isAdmin = "admin".equals(role);
        Map<String, Object> data = new HashMap<>();
        data.put("isAdmin", isAdmin);
        data.put("role", role != null ? role : "user");
        return Result.success(data);
    }

    @GetMapping("/users")
    public Result<?> listUsers(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAdmin(token)) {
            return Result.error("无权限，仅管理员可访问");
        }
        List<Map<String, Object>> users = adminMapper.findAllUsers();
        for (Map<String, Object> user : users) {
            String username = (String) user.get("username");
            var stats = userStatsMapper.findByUsername(username);
            if (stats != null) {
                user.put("viewCount", stats.getViewCount());
                user.put("favoriteCount", stats.getFavoriteCount());
                user.put("predictionCount", stats.getPredictionCount());
                user.put("lastLoginDate", stats.getLastLoginDate());
            } else {
                user.put("viewCount", 0);
                user.put("favoriteCount", 0);
                user.put("predictionCount", 0);
                user.put("lastLoginDate", null);
            }
        }
        return Result.success(users);
    }

    @PutMapping("/user/{id}/role")
    public Result<?> updateRole(@RequestHeader(value = "Authorization", required = false) String token,
                                 @PathVariable Integer id,
                                 @RequestBody Map<String, String> params) {
        if (!isAdmin(token)) {
            return Result.error("无权限");
        }
        String role = params.get("role");
        if (role == null || (!"user".equals(role) && !"admin".equals(role))) {
            return Result.error("角色参数无效");
        }
        Emp target = empMapper.findById(id);
        if (target == null) {
            return Result.error("用户不存在");
        }
        int result = adminMapper.updateRole(id, role);
        if (result > 0) {
            return Result.success(role.equals("admin") ? "已设为管理员" : "已取消管理员");
        }
        return Result.error("操作失败");
    }

    @DeleteMapping("/user/{id}")
    public Result<?> deleteUser(@RequestHeader(value = "Authorization", required = false) String token,
                                 @PathVariable Integer id) {
        if (!isAdmin(token)) {
            return Result.error("无权限");
        }
        Emp target = empMapper.findById(id);
        if (target == null) {
            return Result.error("用户不存在");
        }
        int result = adminMapper.deleteUser(id);
        if (result > 0) {
            return Result.success("用户已删除");
        }
        return Result.error("删除失败");
    }

    @PostMapping("/user/reset-password")
    public Result<?> resetPassword(@RequestHeader(value = "Authorization", required = false) String token,
                                    @RequestBody Map<String, Object> params) {
        if (!isAdmin(token)) {
            return Result.error("无权限");
        }
        Integer id = ((Number) params.get("id")).intValue();
        String newPassword = (String) params.get("password");

        if (id == null || newPassword == null || newPassword.length() < 6) {
            return Result.error("密码至少6位");
        }
        Emp target = empMapper.findById(id);
        if (target == null) {
            return Result.error("用户不存在");
        }
        int result = empMapper.updatePassword(id, newPassword);
        if (result > 0) {
            return Result.success("密码已重置");
        }
        return Result.error("重置失败");
    }
}
