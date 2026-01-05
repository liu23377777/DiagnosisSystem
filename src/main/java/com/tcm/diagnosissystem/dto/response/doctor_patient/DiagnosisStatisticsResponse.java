package com.tcm.diagnosissystem.dto.response.doctor_patient;

import java.util.Map;

/**
 * 诊断统计响应
 */
public class DiagnosisStatisticsResponse {
    private Long totalCount;                        // 总记录数
    private Long todayCount;                        // 今日诊断数
    private Long weekCount;                         // 本周诊断数
    private Long monthCount;                        // 本月诊断数
    private Map<String, Long> syndromeDistribution; // 证型分布
    private Map<String, Long> colorDistribution;    // 舌色分布
    private String mostCommonSyndrome;              // 最常见证型
    private Double averageDiagnosisPerWeek;         // 每周平均诊断次数

    // Getter/Setter
    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(Long todayCount) {
        this.todayCount = todayCount;
    }

    public Long getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(Long weekCount) {
        this.weekCount = weekCount;
    }

    public Long getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(Long monthCount) {
        this.monthCount = monthCount;
    }

    public Map<String, Long> getSyndromeDistribution() {
        return syndromeDistribution;
    }

    public void setSyndromeDistribution(Map<String, Long> syndromeDistribution) {
        this.syndromeDistribution = syndromeDistribution;
    }

    public Map<String, Long> getColorDistribution() {
        return colorDistribution;
    }

    public void setColorDistribution(Map<String, Long> colorDistribution) {
        this.colorDistribution = colorDistribution;
    }

    public String getMostCommonSyndrome() {
        return mostCommonSyndrome;
    }

    public void setMostCommonSyndrome(String mostCommonSyndrome) {
        this.mostCommonSyndrome = mostCommonSyndrome;
    }

    public Double getAverageDiagnosisPerWeek() {
        return averageDiagnosisPerWeek;
    }

    public void setAverageDiagnosisPerWeek(Double averageDiagnosisPerWeek) {
        this.averageDiagnosisPerWeek = averageDiagnosisPerWeek;
    }
}
