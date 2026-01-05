// 路径: src/main/java/com/tcm/diagnosissystem/dto/response/TongueDetailResponse.java
package com.tcm.diagnosissystem.dto.response.doctor_patient;

import java.time.LocalDateTime;

public class TongueDetailResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private String imageUrl;

    // AI分析结果
    private String tongueColor;
    private String tongueShape;
    private String coating;
    private String aiSyndrome;
    private String aiAdvice;

    // 医生审核信息
    private Long doctorId;
    private String doctorName;
    private String finalSyndrome;
    private String finalAdvice;
    private Integer status;           // 0=待审核, 1=已审核
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTongueColor() { return tongueColor; }
    public void setTongueColor(String tongueColor) { this.tongueColor = tongueColor; }

    public String getTongueShape() { return tongueShape; }
    public void setTongueShape(String tongueShape) { this.tongueShape = tongueShape; }

    public String getCoating() { return coating; }
    public void setCoating(String coating) { this.coating = coating; }

    public String getAiSyndrome() { return aiSyndrome; }
    public void setAiSyndrome(String aiSyndrome) { this.aiSyndrome = aiSyndrome; }

    public String getAiAdvice() { return aiAdvice; }
    public void setAiAdvice(String aiAdvice) { this.aiAdvice = aiAdvice; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getFinalSyndrome() { return finalSyndrome; }
    public void setFinalSyndrome(String finalSyndrome) { this.finalSyndrome = finalSyndrome; }

    public String getFinalAdvice() { return finalAdvice; }
    public void setFinalAdvice(String finalAdvice) { this.finalAdvice = finalAdvice; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
