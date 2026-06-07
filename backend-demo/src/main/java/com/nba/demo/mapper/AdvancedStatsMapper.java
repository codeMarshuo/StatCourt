package com.nba.demo.mapper;

import com.nba.demo.entity.AdvancedStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AdvancedStatsMapper {
    
    @Select("SELECT * FROM nba_advanced_stats ORDER BY per DESC")
    List<AdvancedStats> findAll();
    
    @Select("SELECT * FROM nba_advanced_stats WHERE id = #{id}")
    AdvancedStats findById(Integer id);
    
    @Select("SELECT * FROM nba_advanced_stats WHERE player = #{playerName}")
    AdvancedStats findByPlayerName(String playerName);
    
    @Select("SELECT * FROM nba_advanced_stats WHERE team = #{team} ORDER BY per DESC")
    List<AdvancedStats> findByTeam(String team);
    
    @Select("SELECT * FROM nba_advanced_stats ORDER BY per DESC LIMIT #{limit}")
    List<AdvancedStats> findTopByPer(int limit);
    
    @Select("SELECT * FROM nba_advanced_stats ORDER BY ws DESC LIMIT #{limit}")
    List<AdvancedStats> findTopByWs(int limit);
    
    @Select("SELECT * FROM nba_advanced_stats ORDER BY bpm DESC LIMIT #{limit}")
    List<AdvancedStats> findTopByBpm(int limit);
}
