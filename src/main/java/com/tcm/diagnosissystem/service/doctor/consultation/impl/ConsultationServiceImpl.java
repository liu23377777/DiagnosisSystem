package com.tcm.diagnosissystem.service.doctor.consultation.impl;

import com.tcm.diagnosissystem.dto.request.doctor.ConsultationCreateRequest;
import com.tcm.diagnosissystem.dto.response.doctor_patient.ConsultationResponse;
import com.tcm.diagnosissystem.entity.Consultation;
import com.tcm.diagnosissystem.mapper.patient.ConsultationMapper;
import com.tcm.diagnosissystem.service.doctor.consultation.ConsultationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationMapper consultationMapper;

    @Override
    @Transactional
    public ConsultationResponse create(Long userId, ConsultationCreateRequest request) {
        // 1. 构建Consultation对象
        Consultation consultation = new Consultation();
        consultation.setUserId(userId);
        consultation.setChiefComplaint(request.getChiefComplaint());
        consultation.setPresentIllness(request.getPresentIllness());
        consultation.setPastHistory(request.getPastHistory());
        consultation.setAllergyHistory(request.getAllergyHistory());
        consultation.setTongueDescription(request.getTongueDescription());
        consultation.setPulseDescription(request.getPulseDescription());
        consultation.setOtherSymptoms(request.getOtherSymptoms());
        consultation.setStatus(0);  // 0-待诊断

        LocalDateTime now = LocalDateTime.now();
        consultation.setCreateTime(now);
        consultation.setUpdateTime(now);

        // 2. 插入数据库
        int rows = consultationMapper.insert(consultation);

        if (rows == 0) {
            throw new RuntimeException("创建问诊记录失败");
        }

        // 3. 转换为响应对象
        return convertToResponse(consultation);
    }

    @Override
    public ConsultationResponse getById(Long id, Long userId) {
        Consultation consultation = consultationMapper.findById(id);

        if (consultation == null) {
            throw new RuntimeException("问诊记录不存在");
        }

        // 验证是否是本人的记录
        if (!consultation.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此问诊记录");
        }

        return convertToResponse(consultation);
    }

    @Override
    public List<ConsultationResponse> getMyConsultations(Long userId) {
        List<Consultation> consultations = consultationMapper.findByUserId(userId);

        return consultations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationResponse> getMyConsultationsWithPage(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Consultation> consultations = consultationMapper.findByUserIdWithPage(userId, offset, size);

        return consultations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Consultation consultation = consultationMapper.findById(id);

        if (consultation == null) {
            throw new RuntimeException("问诊记录不存在");
        }

        // 验证是否是本人的记录
        if (!consultation.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此问诊记录");
        }

        int rows = consultationMapper.deleteById(id);

        if (rows == 0) {
            throw new RuntimeException("删除失败");
        }
    }

    /**
     * 转换为响应对象
     */
    private ConsultationResponse convertToResponse(Consultation consultation) {
        ConsultationResponse response = new ConsultationResponse();
        BeanUtils.copyProperties(consultation, response);
        response.setStatus(consultation.getStatus());  // 触发statusText自动设置
        return response;
    }
}
