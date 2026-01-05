package com.tcm.diagnosissystem.controller.patient;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.mapper.patient.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 科室相关接口
 */
@RestController
@RequestMapping("/api/department")
public class DepartmentApiController {

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 获取全部科室列表，患者通用
     * GET /api/department/all
     */
    @GetMapping("/all")
    public Result<?> listAll(){
        return Result.success(departmentMapper.selectAll());
    }
}

