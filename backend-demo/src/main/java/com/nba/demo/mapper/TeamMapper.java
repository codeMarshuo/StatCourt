package com.nba.demo.mapper;

import com.nba.demo.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface TeamMapper {
    
    @Select("SELECT * FROM nbateamstats ORDER BY teamrank")
    List<Team> findAll();
    
    @Select("SELECT * FROM nbateamstats WHERE teamid = #{teamid}")
    Team findById(Integer teamid);
}
