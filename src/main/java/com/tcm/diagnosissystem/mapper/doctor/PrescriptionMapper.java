package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.Prescription;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrescriptionMapper {

    @Insert("INSERT INTO prescription(patient_id, doctor_id, diagnosis_id, prescription_no, prescription_type, total_amount, status) " +
            "VALUES(#{patientId}, #{doctorId}, #{diagnosisId}, #{prescriptionNo}, #{prescriptionType}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Prescription p);

    @Select("SELECT * FROM prescription WHERE id=#{id}")
    Prescription selectById(@Param("id") Long id);

    @Select("SELECT * FROM prescription WHERE doctor_id=#{doctorId} ORDER BY create_time DESC")
    List<Prescription> selectByDoctorId(@Param("doctorId") Long doctorId);
}
