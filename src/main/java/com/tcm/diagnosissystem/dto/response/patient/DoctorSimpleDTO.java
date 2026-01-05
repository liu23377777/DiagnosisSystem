package com.tcm.diagnosissystem.dto.response.patient;

public class DoctorSimpleDTO {
    private Long id;
    private String realName;
    private String title;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getRealName() {return realName;}
    public void setRealName(String realName) {this.realName = realName;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
}
