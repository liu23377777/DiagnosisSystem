package com.tcm.diagnosissystem.controller.patient;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.service.patient.AppointmentService;
import com.tcm.diagnosissystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/** 患者创建预约接口 */
@RestController
@RequestMapping("/api/appointment")
public class PatientAppointmentApiController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private JwtUtil jwtUtil;

    /**
     * 预约创建
     * POST /api/appointment
     * body: {doctorId, deptId, time}
     */
    @PostMapping
    public Result<?> create(@RequestHeader("Authorization") String auth,
                            @RequestBody Map<String,String> body){
        Long uid = jwtUtil.getUserIdFromToken(auth.replace("Bearer ",""));
        Long doctorId = Long.valueOf(body.get("doctorId"));
        Long deptId   = Long.valueOf(body.get("deptId"));
        LocalDateTime time = LocalDateTime.parse(body.get("time"));
        appointmentService.create(uid,doctorId,deptId,time);
        return Result.success("操作成功");
    }
}