package com.tcm.diagnosissystem.service.patient;

import com.tcm.diagnosissystem.entity.patient.Appointment;
import com.tcm.diagnosissystem.mapper.doctor.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 创建预约
     */
    public void create(Long patientId, Long doctorId, Long deptId, LocalDateTime time) {
        if (patientId == null) {
            throw new RuntimeException("未登录");
        }
        if (doctorId == null || deptId == null || time == null) {
            throw new RuntimeException("参数不完整");
        }

        // 冲突检测：同一医生同一时间只能有一个未取消预约
        if (appointmentMapper.existsConflict(doctorId, time) > 0) {
            throw new RuntimeException("该时间段医生已被预约");
        }

        Appointment ap = new Appointment();
        ap.setPatientId(patientId);
        ap.setDoctorId(doctorId);
        ap.setDeptId(deptId);
        ap.setAppointTime(time);
        ap.setStatus(0);

        appointmentMapper.insert(ap);
    }

    /**
     * 患者端：我的预约
     */
    public List<Appointment> listMine(Long patientId) {
        if (patientId == null) {
            throw new RuntimeException("未登录");
        }
        return appointmentMapper.findByPatient(patientId);
    }

    /**
     * 医生端：查看自己的预约
     */
    public List<Appointment> listForDoctor(Long doctorId){
        if(doctorId == null){
            throw new RuntimeException("未登录");
        }
        return appointmentMapper.findByDoctorId(doctorId);
    }
}