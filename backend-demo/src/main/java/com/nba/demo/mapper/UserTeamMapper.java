package com.nba.demo.mapper;

import com.nba.demo.entity.UserTeam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserTeamMapper {

    @Insert("INSERT INTO userteam (user_id, team_name, pg_id, sg_id, sf_id, pf_id, c_id, total_score, total_rebound, total_assist, total_per, total_ws, total_bpm, lineup_rating) " +
            "VALUES (#{userId}, #{teamName}, #{pgId}, #{sgId}, #{sfId}, #{pfId}, #{cId}, #{totalScore}, #{totalRebound}, #{totalAssist}, #{totalPer}, #{totalWs}, #{totalBpm}, #{lineupRating})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserTeam userTeam);

    @Update("UPDATE userteam SET team_name=#{teamName}, pg_id=#{pgId}, sg_id=#{sgId}, sf_id=#{sfId}, pf_id=#{pfId}, c_id=#{cId}, " +
            "total_score=#{totalScore}, total_rebound=#{totalRebound}, total_assist=#{totalAssist}, " +
            "total_per=#{totalPer}, total_ws=#{totalWs}, total_bpm=#{totalBpm}, lineup_rating=#{lineupRating} WHERE id=#{id}")
    int update(UserTeam userTeam);

    @Delete("DELETE FROM userteam WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM userteam WHERE user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "pgId", column = "pg_id"),
        @Result(property = "sgId", column = "sg_id"),
        @Result(property = "sfId", column = "sf_id"),
        @Result(property = "pfId", column = "pf_id"),
        @Result(property = "cId", column = "c_id"),
        @Result(property = "totalScore", column = "total_score"),
        @Result(property = "totalRebound", column = "total_rebound"),
        @Result(property = "totalAssist", column = "total_assist"),
        @Result(property = "totalPer", column = "total_per"),
        @Result(property = "totalWs", column = "total_ws"),
        @Result(property = "totalBpm", column = "total_bpm"),
        @Result(property = "lineupRating", column = "lineup_rating"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<UserTeam> selectByUserId(Integer userId);

    @Select("SELECT * FROM userteam WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "pgId", column = "pg_id"),
        @Result(property = "sgId", column = "sg_id"),
        @Result(property = "sfId", column = "sf_id"),
        @Result(property = "pfId", column = "pf_id"),
        @Result(property = "cId", column = "c_id"),
        @Result(property = "totalScore", column = "total_score"),
        @Result(property = "totalRebound", column = "total_rebound"),
        @Result(property = "totalAssist", column = "total_assist"),
        @Result(property = "totalPer", column = "total_per"),
        @Result(property = "totalWs", column = "total_ws"),
        @Result(property = "totalBpm", column = "total_bpm"),
        @Result(property = "lineupRating", column = "lineup_rating"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    UserTeam selectById(Integer id);

    @Select("SELECT * FROM userteam")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "pgId", column = "pg_id"),
        @Result(property = "sgId", column = "sg_id"),
        @Result(property = "sfId", column = "sf_id"),
        @Result(property = "pfId", column = "pf_id"),
        @Result(property = "cId", column = "c_id"),
        @Result(property = "totalScore", column = "total_score"),
        @Result(property = "totalRebound", column = "total_rebound"),
        @Result(property = "totalAssist", column = "total_assist"),
        @Result(property = "totalPer", column = "total_per"),
        @Result(property = "totalWs", column = "total_ws"),
        @Result(property = "totalBpm", column = "total_bpm"),
        @Result(property = "lineupRating", column = "lineup_rating"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<UserTeam> selectAll();
}
