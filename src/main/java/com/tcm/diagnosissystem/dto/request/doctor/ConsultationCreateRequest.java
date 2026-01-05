package com.tcm.diagnosissystem.dto.request.doctor;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
    *创建问诊请求
 */
public class ConsultationCreateRequest {
    @NotBlank(message = "主诉不能为空")
    @Size(min = 5,max = 500, message = "主诉长度为5-500个字符")
    private String chiefComplaint;  //主诉

    @Size(max = 1000, message = "现病史不超过1000字符")
    private String presentIllness; //现病史

    @Size(max = 500, message = "既往史不超过500字符")
    private String pastHistory;  //既往史

    @Size(max = 200, message = "过敏史不超过200字符")
    private String allergyHistory; //过敏史

    @Size(max = 200,message = "舌象描述不超过200字符")
    private String tongueDescription; //舌象描述

    @Size(max = 200, message = "脉象描述不超过200字符")
    private String pulseDescription;    // 脉象描述

    @Size(max = 1000,message = "其他症状不超过1000字符")
    private String otherSymptoms;

    // Getter/Setter
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

}
