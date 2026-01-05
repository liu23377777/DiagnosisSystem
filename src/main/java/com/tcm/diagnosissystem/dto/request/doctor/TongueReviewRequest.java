// 路径: src/main/java/com/tcm/diagnosissystem/dto/request/TongueReviewRequest.java
package com.tcm.diagnosissystem.dto.request.doctor;

public class TongueReviewRequest {
    private Long tongueId;           // 舌诊记录ID
    private String finalSyndrome;    // 医生最终诊断
    private String finalAdvice;      // 医生最终建议

    // Getter/Setter
    public Long getTongueId() { return tongueId; }
    public void setTongueId(Long tongueId) { this.tongueId = tongueId; }

    public String getFinalSyndrome() { return finalSyndrome; }
    public void setFinalSyndrome(String finalSyndrome) { this.finalSyndrome = finalSyndrome; }

    public String getFinalAdvice() { return finalAdvice; }
    public void setFinalAdvice(String finalAdvice) { this.finalAdvice = finalAdvice; }
}
