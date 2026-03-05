package com.nba.demo.mapper;

import com.nba.demo.entity.NbaAdvancedStats;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NbaAdvancedStatsMapper {

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "ORDER BY a.${orderBy} ${orderDir} LIMIT #{limit}")
    List<NbaAdvancedStats> findAllOrderBy(@Param("orderBy") String orderBy, @Param("orderDir") String orderDir, @Param("limit") int limit);

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "ORDER BY a.ws DESC LIMIT #{limit}")
    List<NbaAdvancedStats> findTopByWs(@Param("limit") int limit);

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "ORDER BY a.per DESC LIMIT #{limit}")
    List<NbaAdvancedStats> findTopByPer(@Param("limit") int limit);

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "ORDER BY a.vorp DESC LIMIT #{limit}")
    List<NbaAdvancedStats> findTopByVorp(@Param("limit") int limit);

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "WHERE a.team = #{team} ORDER BY a.ws DESC")
    List<NbaAdvancedStats> findByTeam(@Param("team") String team);

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "WHERE a.player LIKE CONCAT('%', #{name}, '%')")
    List<NbaAdvancedStats> findByName(@Param("name") String name);

    @Select("SELECT COUNT(*) FROM nba_advanced_stats")
    int count();

    @Select("SELECT a.*, b.PlayerImage as playerImage, b.PlayerRank as playerRank " +
            "FROM nba_advanced_stats a " +
            "LEFT JOIN nbaplayerbasicstatistics b ON a.player COLLATE utf8mb4_unicode_ci = b.PlayerName COLLATE utf8mb4_unicode_ci " +
            "WHERE a.id = #{id}")
    NbaAdvancedStats findById(@Param("id") Integer id);
}
