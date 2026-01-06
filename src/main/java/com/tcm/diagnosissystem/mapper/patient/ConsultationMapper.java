package com.tcm.diagnosissystem.mapper.patient;

import com.tcm.diagnosissystem.entity.patient.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ConsultationMapper 统一由 XML (mapper/ConsultationMapper.xml) 管理 SQL，
 * 这里不再使用注解方式，以避免重复声明导致的 MappedStatement 冲突。
 */
@Mapper
public interface ConsultationMapper {

    /* ========= 写操作 ========= */
    int insert(Consultation consultation);
    int update(Consultation consultation);
    int deleteById(Long id);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /* ========= 按 ID ========= */
    Consultation findById(Long id);

    /* ========= 患者侧 ========= */
    List<Consultation> findByUserId(Long userId);
    List<Consultation> findByUserIdWithPage(@Param("userId") Long userId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);
    int countByUserId(Long userId);

    /* ========= 医生侧 ========= */
    List<Consultation> findByDoctorId(Long doctorId);
    List<Consultation> findByDoctorIdWithPage(@Param("doctorId") Long doctorId,
                                              @Param("offset") int offset,
                                              @Param("size")   int size);
}