package com.tcm.diagnosissystem.mapper.patient;

import com.tcm.diagnosissystem.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(@Param("phone") String phone);


    /**
     * 更新用户信息
     * @return 影响的行数
     */
    int updateUser(User user);

    /**
     * 插入新用户
     * @return 影响的行数
     */
    int insertUser(User user);
}
