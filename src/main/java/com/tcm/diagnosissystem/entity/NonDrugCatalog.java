package com.tcm.diagnosissystem.entity;

import java.math.BigDecimal;

/**
 * 对应表 non_drug_catalog_sql_results
 */
public class NonDrugCatalog {

    private Long id;
    private String itemCode;
    private String itemName;
    private String specification;
    private BigDecimal unitPrice;
    private String costCategory;
    private String executionDept;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCostCategory() {
        return costCategory;
    }

    public void setCostCategory(String costCategory) {
        this.costCategory = costCategory;
    }

    public String getExecutionDept() {
        return executionDept;
    }

    public void setExecutionDept(String executionDept) {
        this.executionDept = executionDept;
    }
}

