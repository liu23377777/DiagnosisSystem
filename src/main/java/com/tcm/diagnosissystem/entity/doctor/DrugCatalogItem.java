package com.tcm.diagnosissystem.entity.doctor;

import java.math.BigDecimal;

public class DrugCatalogItem {
    private Long id;
    private String drugCode;
    private String drugName;
    private String specification;
    private String manufacturer;
    private String dosageForm;
    private String approvalNumber;
    private BigDecimal price;
    private String pinyincode;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getDrugCode() {return drugCode;}
    public void setDrugCode(String drugCode) {this.drugCode = drugCode;}
    public String getDrugName() {return drugName;}
    public void setDrugName(String drugName) {this.drugName = drugName;}
    public String getSpecification() {return specification;}
    public void setSpecification(String specification) {this.specification = specification;}
    public String getManufacturer() {return manufacturer;}
    public void setManufacturer(String manufacturer) {this.manufacturer = manufacturer;}
    public String getDosageForm() {return dosageForm;}
    public void setDosageForm(String dosageForm) {this.dosageForm = dosageForm;}
    public String getApprovalNumber() {return approvalNumber;}
    public void setApprovalNumber(String approvalNumber) {this.approvalNumber = approvalNumber;}
    public BigDecimal getPrice() {return price;}
    public void setPrice(BigDecimal price) {this.price = price;}
    public String getPinyincode() {return pinyincode;}
    public void setPinyincode(String pinyincode) {this.pinyincode = pinyincode;}
}
