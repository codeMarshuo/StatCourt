package com.nba.demo.mapper;

import com.nba.demo.entity.Player;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PlayerMapper {
    
    @Select("SELECT * FROM nbaplayerbasicstatistics ORDER BY PlayerRank")
    List<Player> findAll();
    
    @Select("SELECT * FROM nbaplayerbasicstatistics WHERE id = #{id}")
    Player findById(Integer id);
    
    @Select("SELECT * FROM nbaplayerbasicstatistics WHERE Team = #{team} ORDER BY PlayerRank")
    List<Player> findByTeam(String team);
    
    @Select("SELECT * FROM nbaplayerbasicstatistics WHERE Team = #{team} ORDER BY Score DESC LIMIT 3")
    List<Player> findTop3ByTeam(String team);
}
