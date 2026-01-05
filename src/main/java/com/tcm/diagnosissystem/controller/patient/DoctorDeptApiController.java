package com.tcm.diagnosissystem.controller.patient;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.Department;
import com.tcm.diagnosissystem.mapper.patient.DepartmentMapper;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 科室-医生查询接口（给患者预约页面使用）
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorDeptApiController {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 根据科室id查询医生列表
     * GET /api/doctor/by-dept/{id}
     */
    @GetMapping("/by-dept/{id}")
    public Result<?> doctorByDept(@PathVariable Long id){
        Department dept = departmentMapper.selectById(id);
        if(dept == null){
            return Result.failed("科室不存在");
        }
        String deptName = dept.getName();
        return Result.success(doctorMapper.findByDeptName(deptName));
    }
}

