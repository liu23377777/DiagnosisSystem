package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.patient.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约 Mapper
 */
@Mapper
public interface AppointmentMapper {

    int insert(Appointment a);

    Appointment findById(Long id);

    List<Appointment> findByPatient(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    /** 医生同一时段是否已有预约 */
    int existsConflict(@Param("doctorId") Long doctorId,
                       @Param("appointTime") LocalDateTime appointTime);

    /** 删除预约 */
    int deleteById(Long id);
}

