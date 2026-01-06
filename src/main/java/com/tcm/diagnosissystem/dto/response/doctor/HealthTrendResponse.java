package com.tcm.diagnosissystem.dto.response.doctor;

import com.tcm.diagnosissystem.entity.patient.TongueDiagnosis;
import java.util.List;
import java.util.Map;

/**
 * 健康趋势分析响应
 */
public class HealthTrendResponse {
    private Integer recordCount;                    // 记录总数
    private Integer analyzeDays;                    // 分析天数
    private Map<String, Long> syndromeDistribution; // 证型分布统计
    private List<String> colorTrend;                // 舌色变化趋势
    private List<String> coatingTrend;              // 舌苔变化趋势
    private TongueDiagnosis latestDiagnosis;        // 最新诊断
    private TongueDiagnosis earliestDiagnosis;      // 最早诊断
    private String trendAnalysis;                   // AI趋势分析
    private String healthStatus;                    // 健康状态：improving/stable/declining
    private List<String> suggestions;               // 改善建议

    // Getter/Setter
    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public Integer getAnalyzeDays() {
        return analyzeDays;
    }

    public void setAnalyzeDays(Integer analyzeDays) {
        this.analyzeDays = analyzeDays;
    }

    public Map<String, Long> getSyndromeDistribution() {
        return syndromeDistribution;
    }

    public void setSyndromeDistribution(Map<String, Long> syndromeDistribution) {
        this.syndromeDistribution = syndromeDistribution;
    }

    public List<String> getColorTrend() {
        return colorTrend;
    }

    public void setColorTrend(List<String> colorTrend) {
        this.colorTrend = colorTrend;
    }

    public List<String> getCoatingTrend() {
        return coatingTrend;
    }

    public void setCoatingTrend(List<String> coatingTrend) {
        this.coatingTrend = coatingTrend;
    }

    public TongueDiagnosis getLatestDiagnosis() {
        return latestDiagnosis;
    }

    public void setLatestDiagnosis(TongueDiagnosis latestDiagnosis) {
        this.latestDiagnosis = latestDiagnosis;
    }

    public TongueDiagnosis getEarliestDiagnosis() {
        return earliestDiagnosis;
    }

    public void setEarliestDiagnosis(TongueDiagnosis earliestDiagnosis) {
        this.earliestDiagnosis = earliestDiagnosis;
    }

    public String getTrendAnalysis() {
        return trendAnalysis;
    }

    public void setTrendAnalysis(String trendAnalysis) {
        this.trendAnalysis = trendAnalysis;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
