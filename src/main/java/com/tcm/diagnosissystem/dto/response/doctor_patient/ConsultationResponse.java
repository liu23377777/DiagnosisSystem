package com.tcm.diagnosissystem.dto.response.doctor_patient;


import java.time.LocalDateTime;

/**
 * 问诊记录响应
 */
public class ConsultationResponse {

    private Long id;
    private Long userId;
    private String chiefComplaint;
    private String presentIllness;
    private String pastHistory;
    private String allergyHistory;
    private String tongueDescription;
    private String pulseDescription;
    private String otherSymptoms;
    private Integer status;              // 0-待诊断，1-已诊断，2-已关闭
    private String statusText;           // 状态文本
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getter/Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getPresentIllness() {
        return presentIllness;
    }

    public void setPresentIllness(String presentIllness) {
        this.presentIllness = presentIllness;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public void setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
    }

    public String getAllergyHistory() {
        return allergyHistory;
    }

    public void setAllergyHistory(String allergyHistory) {
        this.allergyHistory = allergyHistory;
    }

    public String getTongueDescription() {
        return tongueDescription;
    }

    public void setTongueDescription(String tongueDescription) {
        this.tongueDescription = tongueDescription;
    }

    public String getPulseDescription() {
        return pulseDescription;
    }

    public void setPulseDescription(String pulseDescription) {
        this.pulseDescription = pulseDescription;
    }

    public String getOtherSymptoms() {
        return otherSymptoms;
    }

    public void setOtherSymptoms(String otherSymptoms) {
        this.otherSymptoms = otherSymptoms;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
        // 自动设置状态文本
        switch (status) {
            case 0:
                this.statusText = "待诊断";
                break;
            case 1:
                this.statusText = "已诊断";
                break;
            case 2:
                this.statusText = "已关闭";
                break;
            default:
                this.statusText = "未知";
        }
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
