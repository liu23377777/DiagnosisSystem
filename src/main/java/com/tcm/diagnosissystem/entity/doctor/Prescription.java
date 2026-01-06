package com.tcm.diagnosissystem.entity.doctor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对应数据库表 prescription
 */
public class Prescription {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long diagnosisId;
    private String prescriptionNo;
    private String diagnosisIcd;
    private String diagnosisName;
    private String prescriptionType;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getPatientId() {return patientId;}
    public void setPatientId(Long patientId) {this.patientId = patientId;}
    public Long getDoctorId() {return doctorId;}
    public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}
    public Long getDiagnosisId() {return diagnosisId;}
    public void setDiagnosisId(Long diagnosisId) {this.diagnosisId = diagnosisId;}
    public String getPrescriptionNo() {return prescriptionNo;}
    public void setPrescriptionNo(String prescriptionNo) {this.prescriptionNo = prescriptionNo;}
    public String getDiagnosisIcd() {return diagnosisIcd;}
    public void setDiagnosisIcd(String diagnosisIcd) {this.diagnosisIcd = diagnosisIcd;}
    public String getDiagnosisName() {return diagnosisName;}
    public void setDiagnosisName(String diagnosisName) {this.diagnosisName = diagnosisName;}
    public String getPrescriptionType() {return prescriptionType;}
    public void setPrescriptionType(String prescriptionType) {this.prescriptionType = prescriptionType;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public LocalDateTime getCreateTime() {return createTime;}
    public void setCreateTime(LocalDateTime createTime) {this.createTime = createTime;}
    public LocalDateTime getUpdateTime() {return updateTime;}
    public void setUpdateTime(LocalDateTime updateTime) {this.updateTime = updateTime;}
}
