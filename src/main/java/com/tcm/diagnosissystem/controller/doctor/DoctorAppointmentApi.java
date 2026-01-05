package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.service.patient.AppointmentService;
import com.tcm.diagnosissystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生端预约接口
 */
@RestController
@RequestMapping("/api/doctor/appointment")
public class DoctorAppointmentApi {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取我的预约
    @GetMapping("/my")
    public Result<?> my(@RequestHeader("Authorization") String auth){
        Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ",""));
        return Result.success(appointmentService.listForDoctor(doctorId));
    }
}

