// 路径: src/main/java/com/tcm/diagnosissystem/service/TongueReviewService.java
package com.tcm.diagnosissystem.service.doctor;

import com.tcm.diagnosissystem.dto.request.doctor.TongueReviewRequest;
import com.tcm.diagnosissystem.dto.response.doctor_patient.TongueDetailResponse;
import com.tcm.diagnosissystem.entity.Patient;
import com.tcm.diagnosissystem.entity.Doctor;
import com.tcm.diagnosissystem.entity.TongueDiagnosis;
import com.tcm.diagnosissystem.mapper.patient.PatientMapper;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import com.tcm.diagnosissystem.mapper.doctor.TongueDiagnosisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TongueReviewService {

    @Autowired
    private TongueDiagnosisMapper tongueMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 获取待审核的舌诊列表
     */
    public List<TongueDetailResponse> getPendingReviews() {
        List<TongueDiagnosis> pendingList = tongueMapper.selectPendingReviews();
        return convertToResponseList(pendingList);
    }

    /**
     * 获取医生已审核的记录
     */
    public List<TongueDetailResponse> getReviewedByDoctor(Long doctorId) {
        List<TongueDiagnosis> reviewedList = tongueMapper.selectReviewedByDoctor(doctorId);
        return convertToResponseList(reviewedList);
    }

    /**
     * 获取舌诊详情
     */
    public TongueDetailResponse getTongueDetail(Long tongueId) {
        TongueDiagnosis tongue = tongueMapper.selectById(tongueId);
        if (tongue == null) {
            throw new RuntimeException("舌诊记录不存在");
        }
        return convertToResponse(tongue);
    }

    /**
     * 医生审核舌诊
     */
    @Transactional
    public TongueDetailResponse reviewTongue(Long doctorId, TongueReviewRequest request) {
        // 1. 查询舌诊记录
        TongueDiagnosis tongue = tongueMapper.selectById(request.getTongueId());
        if (tongue == null) {
            throw new RuntimeException("舌诊记录不存在");
        }

        // 2. 检查是否已审核
        if (tongue.getStatus() != null && tongue.getStatus() == 1) {
            throw new RuntimeException("该舌诊记录已被审核");
        }

        // 3. 更新审核信息
        tongue.setDoctorId(doctorId);
        tongue.setFinalSyndrome(request.getFinalSyndrome());
        tongue.setFinalAdvice(request.getFinalAdvice());
        tongue.setReviewTime(LocalDateTime.now());

        int updated = tongueMapper.updateReview(tongue);
        if (updated == 0) {
            throw new RuntimeException("审核失败");
        }

        // 4. 返回更新后的详情
        return getTongueDetail(request.getTongueId());
    }

    /**
     * 转换为Response列表
     */
    private List<TongueDetailResponse> convertToResponseList(List<TongueDiagnosis> list) {
        List<TongueDetailResponse> responseList = new ArrayList<>();
        for (TongueDiagnosis tongue : list) {
            responseList.add(convertToResponse(tongue));
        }
        return responseList;
    }

    /**
     * 转换为Response对象
     */
    private TongueDetailResponse convertToResponse(TongueDiagnosis tongue) {
        TongueDetailResponse response = new TongueDetailResponse();
        response.setId(tongue.getId());
        response.setPatientId(tongue.getPatientId());
        response.setImageUrl(tongue.getImageUrl());
        response.setTongueColor(tongue.getTongueColor());
        response.setTongueShape(tongue.getTongueShape());
        response.setCoating(tongue.getCoating());
        response.setAiSyndrome(tongue.getSyndrome());
        response.setAiAdvice(tongue.getAdvice());
        response.setDoctorId(tongue.getDoctorId());
        response.setFinalSyndrome(tongue.getFinalSyndrome());
        response.setFinalAdvice(tongue.getFinalAdvice());
        response.setStatus(tongue.getStatus());
        response.setReviewTime(tongue.getReviewTime());
        response.setCreateTime(tongue.getCreateTime());

        // 查询患者姓名
        if (tongue.getPatientId() != null) {
            Patient patient = patientMapper.selectById(tongue.getPatientId());
            if (patient != null) {
                response.setPatientName(patient.getRealName());
            }
        }

        // 查询医生姓名
        if (tongue.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(tongue.getDoctorId());
            if (doctor != null) {
                response.setDoctorName(doctor.getRealName());
            }
        }

        return response;
    }
}
