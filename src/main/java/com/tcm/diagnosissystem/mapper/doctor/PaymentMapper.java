package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.patient.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付单 Mapper
 */
@Mapper
public interface PaymentMapper {

    int insert(Payment p);

    Payment findById(Long id);

    Payment findByAppointmentId(Long appointmentId);

    List<Payment> findByPatientId(Long patientId);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 按预约删除支付单（用于删除预约时级联） */
    int deleteByAppointmentId(Long appointmentId);
}

