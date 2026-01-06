// 路径: src/main/java/com/tcm/diagnosissystem/service/PatientService.java
package com.tcm.diagnosissystem.service.patient;

import com.tcm.diagnosissystem.entity.patient.Patient;
import com.tcm.diagnosissystem.mapper.patient.PatientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientService {

    @Autowired
    private PatientMapper patientMapper;

    public Patient getPatientByUsername(String username) {
        return patientMapper.selectByUsername(username);
    }

    public Patient getPatientById(Long id) {
        return patientMapper.selectById(id);
    }

    public Patient getPatientByPhone(String phone) {
        return patientMapper.selectByPhone(phone);
    }

    public int registerPatient(Patient patient) {
        return patientMapper.insert(patient);
    }

    public int updatePatient(Patient patient) {
        return patientMapper.update(patient);
    }

    public int deletePatient(Long id) {
        return patientMapper.delete(id);
    }
}
