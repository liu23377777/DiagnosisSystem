package com.tcm.diagnosissystem.dto.request.doctor;

import java.math.BigDecimal;
import java.util.List;

public class PrescriptionCreateRequest {
    private Long patientId;
    private Long diagnosisId; // 可选
    private List<Item> items;

    public Long getPatientId() {return patientId;}
    public void setPatientId(Long patientId) {this.patientId = patientId;}
    public Long getDiagnosisId() {return diagnosisId;}
    public void setDiagnosisId(Long diagnosisId) {this.diagnosisId = diagnosisId;}
    public List<Item> getItems() {return items;}
    public void setItems(List<Item> items) {this.items = items;}

    public static class Item {
        private String drugCode;
        private String drugName;
        private String dosage;
        private Integer quantity;
        private BigDecimal unitPrice;

        public String getDrugCode() {return drugCode;}
        public void setDrugCode(String drugCode) {this.drugCode = drugCode;}
        public String getDrugName() {return drugName;}
        public void setDrugName(String drugName) {this.drugName = drugName;}
        public String getDosage() {return dosage;}
        public void setDosage(String dosage) {this.dosage = dosage;}
        public Integer getQuantity() {return quantity;}
        public void setQuantity(Integer quantity) {this.quantity = quantity;}
        public BigDecimal getUnitPrice() {return unitPrice;}
        public void setUnitPrice(BigDecimal unitPrice) {this.unitPrice = unitPrice;}
    }
}