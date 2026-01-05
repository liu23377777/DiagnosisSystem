package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.Appointment;
import com.tcm.diagnosissystem.mapper.doctor.AppointmentMapper;
import com.tcm.diagnosissystem.mapper.doctor.PaymentMapper;
import com.tcm.diagnosissystem.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 仅处理删除预约逻辑
 */
@RestController
@RequestMapping("/api/appointment")
public class AppointmentApiController {

    @Autowired private AppointmentMapper appointmentMapper;
    @Autowired private PaymentMapper paymentMapper;
    @Autowired private JwtUtil jwtUtil;

    /** 医生删除自己的预约，同时级联删除对应支付单 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@RequestHeader("Authorization") String auth,
                            @PathVariable Long id) {
    
        Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ",""));
    
        Appointment a = appointmentMapper.findById(id);
        if(a==null) return Result.failed("记录不存在");
        if(!doctorId.equals(a.getDoctorId())) return Result.failed("无权限删除");
    
        appointmentMapper.deleteById(id);
        paymentMapper.deleteByAppointmentId(id);
        return Result.success("已删除");
    }
}
