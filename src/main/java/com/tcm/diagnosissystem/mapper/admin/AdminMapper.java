// 路径: src/main/java/com/tcm/diagnosissystem/mapper/AdminMapper.java
package com.tcm.diagnosissystem.mapper.admin;

import com.tcm.diagnosissystem.entity.admin.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM administrator WHERE username = #{username}")
    Admin selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM administrator WHERE id = #{id}")
    Admin selectById(@Param("id") Long id);

    /** 更新管理员头像 */
    @Update("UPDATE administrator SET avatar_url=#{avatarUrl}, update_time=#{updateTime} WHERE id=#{id}")
    int updateAvatarUrl(@Param("id") Long id,
                    @Param("avatarUrl") String avatarUrl,
                    @Param("updateTime") java.time.LocalDateTime updateTime);
    
    @Update("UPDATE administrator SET phone=#{phone}, email=#{email}, update_time=#{updateTime} WHERE id=#{id}")
    int update(Admin admin);
}
