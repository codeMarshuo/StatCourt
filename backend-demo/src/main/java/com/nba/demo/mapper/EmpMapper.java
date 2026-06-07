package com.nba.demo.mapper;

import com.nba.demo.entity.Emp;
import org.apache.ibatis.annotations.*;

@Mapper
public interface EmpMapper {
    
    @Select("SELECT * FROM emp WHERE username = #{username}")
    Emp findByUsername(String username);
    
    @Select("SELECT * FROM emp WHERE id = #{id}")
    Emp findById(Integer id);
    
    @Insert("INSERT INTO emp(username, password, name, gender, phone, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{name}, #{gender}, #{phone}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Emp emp);
    
    @Update("UPDATE emp SET name = #{name}, gender = #{gender}, phone = #{phone}, " +
            "image = #{image}, update_time = NOW() WHERE id = #{id}")
    int update(Emp emp);
    
    @Update("UPDATE emp SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
    
    @Select("SELECT COUNT(*) FROM emp WHERE username = #{username}")
    int countByUsername(String username);
}
