package com.tcm.diagnosissystem.entity.patient;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long deptId;
    private LocalDateTime appointTime;
    private Integer status; // 0-待确认 1-已确认 2-已取消
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getPatientId() {return patientId;}
    public void setPatientId(Long patientId) {this.patientId = patientId;}
    public Long getDoctorId() {return doctorId;}
    public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}
    public Long getDeptId() {return deptId;}
    public void setDeptId(Long deptId) {this.deptId = deptId;}
    public LocalDateTime getAppointTime() {return appointTime;}
    public void setAppointTime(LocalDateTime appointTime) {this.appointTime = appointTime;}
    public Integer getStatus() {return status;}
    public void setStatus(Integer status) {this.status = status;}
    public LocalDateTime getCreateTime() {return createTime;}
    public void setCreateTime(LocalDateTime createTime) {this.createTime = createTime;}
}
