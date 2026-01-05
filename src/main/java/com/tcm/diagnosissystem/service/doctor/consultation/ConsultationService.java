package com.tcm.diagnosissystem.service.doctor.consultation;

import com.tcm.diagnosissystem.dto.request.doctor.ConsultationCreateRequest;
import com.tcm.diagnosissystem.dto.response.doctor_patient.ConsultationResponse;

import java.util.List;

public interface ConsultationService {

    /**
     * 创建问诊记录
     */
    ConsultationResponse create(Long userId, ConsultationCreateRequest request);

    /**
     * 获取问诊记录详情
     */
    ConsultationResponse getById(Long id, Long userId);

    /**
     * 获取用户的所有问诊记录
     */
    List<ConsultationResponse> getMyConsultations(Long userId);

    /**
     * 分页获取问诊记录
     */
    List<ConsultationResponse> getMyConsultationsWithPage(Long userId, int page, int size);

    /**
     * 删除问诊记录
     */
    void delete(Long id, Long userId);
}
