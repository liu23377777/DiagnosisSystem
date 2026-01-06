package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.dto.response.patient.DoctorSimpleDTO;
import com.tcm.diagnosissystem.entity.doctor.Doctor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DoctorMapper {

    Doctor selectById(@Param("id") Long id);

    Doctor selectByUsername(@Param("username") String username);

    @Insert("INSERT INTO doctor(username,password,real_name,department,hospital_name,title,status,create_time,update_time) " +
            "VALUES(#{username},#{password},#{realName},#{department},#{hospitalName},#{title},#{status},#{createTime},#{updateTime})")
    int insert(Doctor d);

    @Select("SELECT COUNT(*) FROM doctor")
    long countAll();

    @Select("SELECT COUNT(*) FROM doctor WHERE department=#{deptName}")
    int countByDeptName(String deptName);

    /** 按科室名称查询在岗医生 */
    @Select("SELECT id, real_name, title FROM doctor WHERE status=1 AND department = #{deptName}")
    List<DoctorSimpleDTO> findByDeptName(String deptName);

    /** 更新医生信息 */
    @Update("UPDATE doctor SET phone=#{phone}, email=#{email}, gender=#{gender}, " +
            "hospital_name=#{hospitalName}, department=#{department}, title=#{title}, " +
            "specialty=#{specialty}, update_time=#{updateTime} WHERE id=#{id}")
    int update(Doctor doctor);

    /** 查询所有医生（管理员用） */
    @Select("SELECT * FROM doctor ORDER BY create_time DESC")
    List<Doctor> selectAll();

    /** 更新医生状态 */
    @Update("UPDATE doctor SET status=#{status}, update_time=#{updateTime} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateTime") java.time.LocalDateTime updateTime);

    /** 更新医生头像 */
    @Update("UPDATE doctor SET avatar_url=#{avatarUrl}, update_time=#{updateTime} WHERE id=#{id}")
    int updateAvatarUrl(@Param("id") Long id,
                @Param("avatarUrl") String avatarUrl,
                @Param("updateTime") java.time.LocalDateTime updateTime);   
}