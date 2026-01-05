package com.tcm.diagnosissystem.entity;

import java.time.LocalDateTime;

/**
 * 问诊记录实体类
 */
public class Consultation {

    private Long id;                    // 主键ID
    private Long userId;                // 用户ID（外键）
    private String chiefComplaint;      // 主诉（主要症状）
    private String presentIllness;      // 现病史
    private String pastHistory;         // 既往史
    private String allergyHistory;      // 过敏史
    private String tongueDescription;   // 舌象描述
    private String pulseDescription;    // 脉象描述
    private String otherSymptoms;       // 其他症状
    private Integer status;             // 状态：0-待诊断，1-已诊断，2-已关闭
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间

    // 构造方法
    public Consultation() {
    }

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
