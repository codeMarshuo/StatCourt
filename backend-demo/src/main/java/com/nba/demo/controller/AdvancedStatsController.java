package com.nba.demo.controller;

import com.nba.demo.entity.NbaAdvancedStats;
import com.nba.demo.mapper.NbaAdvancedStatsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced")
@CrossOrigin(origins = "*")
public class AdvancedStatsController {

    private static final Logger log = LoggerFactory.getLogger(AdvancedStatsController.class);

    @Autowired
    private NbaAdvancedStatsMapper advancedStatsMapper;

    @GetMapping("/top/ws")
    public Map<String, Object> getTopByWs(@RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NbaAdvancedStats> data = advancedStatsMapper.findTopByWs(limit);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/top/per")
    public Map<String, Object> getTopByPer(@RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NbaAdvancedStats> data = advancedStatsMapper.findTopByPer(limit);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/top/vorp")
    public Map<String, Object> getTopByVorp(@RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NbaAdvancedStats> data = advancedStatsMapper.findTopByVorp(limit);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/team/{team}")
    public Map<String, Object> getByTeam(@PathVariable String team) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NbaAdvancedStats> data = advancedStatsMapper.findByTeam(team);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/search")
    public Map<String, Object> searchByName(@RequestParam String name) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NbaAdvancedStats> data = advancedStatsMapper.findByName(name);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/all")
    public Map<String, Object> getAll(@RequestParam(defaultValue = "ws") String orderBy,
                                       @RequestParam(defaultValue = "desc") String orderDir,
                                       @RequestParam(defaultValue = "50") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            String safeOrderDir = "asc".equalsIgnoreCase(orderDir) ? "ASC" : "DESC";
            List<NbaAdvancedStats> data = advancedStatsMapper.findAllOrderBy(orderBy, safeOrderDir, limit);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            NbaAdvancedStats data = advancedStatsMapper.findById(id);
            if (data != null) {
                result.put("code", 200);
                result.put("message", "查询成功");
                result.put("data", data);
            } else {
                result.put("code", 404);
                result.put("message", "数据不存在");
            }
        } catch (Exception e) {
            log.error("查询失败", e);
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }
}
