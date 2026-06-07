package com.nba.demo.mapper;

import com.nba.demo.entity.UserStats;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;

@Mapper
public interface UserStatsMapper {
    
    @Select("SELECT * FROM user_stats WHERE username = #{username}")
    UserStats findByUsername(String username);
    
    @Insert("INSERT INTO user_stats(username, view_count, favorite_count, prediction_count, create_time, update_time) " +
            "VALUES(#{username}, 0, 0, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "username")
    int insert(UserStats userStats);
    
    @Update("UPDATE user_stats SET view_count = view_count + 1, " +
            "update_time = NOW() WHERE username = #{username}")
    int incrementViewCount(String username);
    
    @Update("UPDATE user_stats SET favorite_count = favorite_count + 1, " +
            "update_time = NOW() WHERE username = #{username}")
    int incrementFavoriteCount(String username);
    
    @Update("UPDATE user_stats SET prediction_count = prediction_count + 1, " +
            "update_time = NOW() WHERE username = #{username}")
    int incrementPredictionCount(String username);
    
    @Update("UPDATE user_stats SET last_login_date = #{lastLoginDate}, " +
            "update_time = NOW() WHERE username = #{username}")
    int updateLastLoginDate(@Param("username") String username, @Param("lastLoginDate") LocalDate lastLoginDate);
    
    @Update("UPDATE user_stats SET favorite_team = #{favoriteTeam}, " +
            "update_time = NOW() WHERE username = #{username}")
    int updateFavoriteTeam(@Param("username") String username, @Param("favoriteTeam") String favoriteTeam);
    
    @Update("UPDATE user_stats SET favorite_player = #{favoritePlayer}, " +
            "update_time = NOW() WHERE username = #{username}")
    int updateFavoritePlayer(@Param("username") String username, @Param("favoritePlayer") String favoritePlayer);
    
    @Update("UPDATE user_stats SET favorite_team = #{favoriteTeam}, favorite_player = #{favoritePlayer}, " +
            "update_time = NOW() WHERE username = #{username}")
    int updateFavorites(@Param("username") String username, 
                        @Param("favoriteTeam") String favoriteTeam, 
                        @Param("favoritePlayer") String favoritePlayer);
}
