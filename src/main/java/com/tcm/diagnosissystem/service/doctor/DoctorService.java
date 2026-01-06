package com.tcm.diagnosissystem.service.doctor;

import com.tcm.diagnosissystem.entity.doctor.Doctor;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    public Doctor getDoctorById(Long id) {
        return doctorMapper.selectById(id);
    }

    public Doctor getDoctorByUsername(String username) {
        return doctorMapper.selectByUsername(username);
    }

    // ===== 审核相关 =====
    public List<Doctor> findPending(){
        return doctorMapper.selectAll().stream().filter(d->d.getStatus()!=null && d.getStatus()==0).toList();
    }

    public int updateStatus(Long id,int status){
        return doctorMapper.updateStatus(id,status, LocalDateTime.now());
    }
}