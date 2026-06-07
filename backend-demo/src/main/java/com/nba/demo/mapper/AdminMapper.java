package com.nba.demo.mapper;

import com.nba.demo.entity.Emp;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    @Select("SELECT id, username, name, gender, phone, role, create_time, update_time FROM emp ORDER BY id")
    List<Map<String, Object>> findAllUsers();

    @Select("SELECT role FROM emp WHERE id = #{id}")
    String findRoleById(Integer id);

    @Select("SELECT role FROM emp WHERE username = #{username}")
    String findRoleByUsername(String username);

    @Update("UPDATE emp SET role = #{role} WHERE id = #{id}")
    int updateRole(@Param("id") Integer id, @Param("role") String role);

    @Delete("DELETE FROM emp WHERE id = #{id}")
    int deleteUser(Integer id);

    @Select("SELECT username FROM user_stats WHERE username = #{username}")
    String findUserStatsByUsername(String username);

    @Select("SELECT id, username, name, gender, phone, role, create_time FROM emp WHERE id = #{id}")
    Map<String, Object> findUserById(Integer id);
}
