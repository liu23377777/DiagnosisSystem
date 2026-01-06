package com.tcm.diagnosissystem.mapper.patient;

import com.tcm.diagnosissystem.entity.doctor.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    @Select("SELECT id, dept_code AS code, dept_name AS name, dept_type AS type FROM hospital_department ORDER BY id")
    List<Department> selectAll();

    @Select("SELECT id, dept_name AS name FROM hospital_department WHERE id = #{id}")
    Department selectById(Long id);
}