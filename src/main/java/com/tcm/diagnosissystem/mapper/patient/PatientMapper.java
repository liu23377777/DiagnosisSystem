// 路径: src/main/java/com/tcm/diagnosissystem/mapper/PatientMapper.java
package com.tcm.diagnosissystem.mapper.patient;

import com.tcm.diagnosissystem.entity.patient.Patient;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PatientMapper {

    @Select("SELECT * FROM patient WHERE username = #{username}")
    Patient selectByUsername(String username);

    @Select("SELECT * FROM patient WHERE id = #{id}")
    Patient selectById(Long id);

    @Select("SELECT * FROM patient WHERE phone = #{phone}")
    Patient selectByPhone(String phone);

    @Insert("INSERT INTO patient(username, password, real_name, phone, email, gender, " +
            "birth_date, address, status, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{realName}, #{phone}, #{email}, #{gender}, " +
            "#{birthDate}, #{address}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Patient patient);

    @Update("UPDATE patient SET real_name=#{realName}, phone=#{phone}, email=#{email}, " +
            "gender=#{gender}, birth_date=#{birthDate}, address=#{address}, " +
            "update_time=#{updateTime} WHERE id=#{id}")
    int update(Patient patient);

    /** 更新患者头像 */
    @Update("UPDATE patient SET avatar_url=#{avatarUrl}, update_time=#{updateTime} WHERE id=#{id}")
    int updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl, @Param("updateTime") java.time.LocalDateTime updateTime);

    @Delete("DELETE FROM patient WHERE id = #{id}")
    int delete(Long id);

    /** 查询所有患者（管理员用） */
    @Select("SELECT * FROM patient ORDER BY create_time DESC")
    List<Patient> selectAll();

    /** 更新患者状态 */
    @Update("UPDATE patient SET status=#{status}, update_time=#{updateTime} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateTime") java.time.LocalDateTime updateTime);
}
