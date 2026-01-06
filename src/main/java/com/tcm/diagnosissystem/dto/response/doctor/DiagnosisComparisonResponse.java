package com.tcm.diagnosissystem.dto.response.doctor;

import com.tcm.diagnosissystem.entity.patient.TongueDiagnosis;
import java.util.List;

/**
 * 诊断对比响应
 */
public class DiagnosisComparisonResponse {
    private TongueDiagnosis oldDiagnosis;       // 旧诊断
    private TongueDiagnosis newDiagnosis;       // 新诊断
    private List<String> differences;            // 差异点列表
    private String changeAnalysis;               // 变化分析
    private String progressStatus;               // 进展状态：improved/worsened/stable
    private Integer daysBetween;                 // 相隔天数

    // Getter/Setter
    public TongueDiagnosis getOldDiagnosis() {
        return oldDiagnosis;
    }

    public void setOldDiagnosis(TongueDiagnosis oldDiagnosis) {
        this.oldDiagnosis = oldDiagnosis;
    }

    public TongueDiagnosis getNewDiagnosis() {
        return newDiagnosis;
    }

    public void setNewDiagnosis(TongueDiagnosis newDiagnosis) {
        this.newDiagnosis = newDiagnosis;
    }

    public List<String> getDifferences() {
        return differences;
    }

    public void setDifferences(List<String> differences) {
        this.differences = differences;
    }

    public String getChangeAnalysis() {
        return changeAnalysis;
    }

    public void setChangeAnalysis(String changeAnalysis) {
        this.changeAnalysis = changeAnalysis;
    }

    public String getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Integer getDaysBetween() {
        return daysBetween;
    }

    public void setDaysBetween(Integer daysBetween) {
        this.daysBetween = daysBetween;
    }
}
