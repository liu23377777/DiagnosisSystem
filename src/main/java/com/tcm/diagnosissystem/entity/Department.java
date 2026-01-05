package com.tcm.diagnosissystem.entity;

public class Department {
    private Long id;
    private String code;
    private String name;
    private String type;

    // getter setter
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
}