// 路径: src/main/java/com/tcm/diagnosissystem/entity/TongueDiagnosis.java
package com.tcm.diagnosissystem.entity;

import java.time.LocalDateTime;

public class TongueDiagnosis {
    private Long id;
    private Long userId;             // 用户ID（与XML映射一致）
    private Long patientId;          // 患者ID（兼容旧代码）
    private String imageUrl;
    private String tongueColor;
    private String tongueShape;
    private String coating;          // 兼容旧字段
    private String coatingColor;      // 舌苔颜色
    private String coatingQuality;  // 舌苔质地
    private String syndrome;         // AI诊断的证型
    private String pathogenesis;     // 病机分析
    private String advice;           // AI建议
    private String fullAnalysis;     // 完整分析JSON
    private LocalDateTime createTime;

    // 新增医生审核字段
    private Long doctorId;
    private String finalSyndrome;    // 医生最终诊断
    private String finalAdvice;      // 医生最终建议
    private Integer status;          // 0=待审核, 1=已审核
    private LocalDateTime reviewTime;

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTongueColor() { return tongueColor; }
    public void setTongueColor(String tongueColor) { this.tongueColor = tongueColor; }

    public String getTongueShape() { return tongueShape; }
    public void setTongueShape(String tongueShape) { this.tongueShape = tongueShape; }

    public String getCoating() { return coating; }
    public void setCoating(String coating) { this.coating = coating; }

    public String getCoatingColor() { return coatingColor; }
    public void setCoatingColor(String coatingColor) { this.coatingColor = coatingColor; }

    public String getCoatingQuality() { return coatingQuality; }
    public void setCoatingQuality(String coatingQuality) { this.coatingQuality = coatingQuality; }

    public String getPathogenesis() { return pathogenesis; }
    public void setPathogenesis(String pathogenesis) { this.pathogenesis = pathogenesis; }

    public String getFullAnalysis() { return fullAnalysis; }
    public void setFullAnalysis(String fullAnalysis) { this.fullAnalysis = fullAnalysis; }

    public String getSyndrome() { return syndrome; }
    public void setSyndrome(String syndrome) { this.syndrome = syndrome; }

    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getFinalSyndrome() { return finalSyndrome; }
    public void setFinalSyndrome(String finalSyndrome) { this.finalSyndrome = finalSyndrome; }

    public String getFinalAdvice() { return finalAdvice; }
    public void setFinalAdvice(String finalAdvice) { this.finalAdvice = finalAdvice; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
}
