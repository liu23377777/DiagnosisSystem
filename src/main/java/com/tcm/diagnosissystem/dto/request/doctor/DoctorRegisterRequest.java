package com.tcm.diagnosissystem.dto.request.doctor;

import jakarta.validation.constraints.*;

/**
 * 医生注册请求对象
 */
public class DoctorRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3-20个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名过长")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private Integer gender;

    @NotBlank(message = "医院名称不能为空")
    private String hospitalName;

    @NotBlank(message = "科室不能为空")
    private String department;

    @NotBlank(message = "职称不能为空")
    private String title;

    private String specialty; // 专长（可选）

    public @NotBlank(message = "用户名不能为空") @Size(min = 3, max = 20, message = "用户名长度为3-20个字符") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "用户名不能为空") @Size(min = 3, max = 20, message = "用户名长度为3-20个字符") String username) {
        this.username = username;
    }

    public @NotBlank(message = "密码不能为空") @Size(min = 6, max = 20, message = "密码长度为6-20个字符") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "密码不能为空") @Size(min = 6, max = 20, message = "密码长度为6-20个字符") String password) {
        this.password = password;
    }

    public @NotBlank(message = "确认密码不能为空") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NotBlank(message = "确认密码不能为空") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public @NotBlank(message = "真实姓名不能为空") @Size(max = 50, message = "姓名过长") String getRealName() {
        return realName;
    }

    public void setRealName(@NotBlank(message = "真实姓名不能为空") @Size(max = 50, message = "姓名过长") String realName) {
        this.realName = realName;
    }

    public @NotBlank(message = "手机号不能为空") @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "手机号不能为空") @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        this.phone = phone;
    }

    public @Email(message = "邮箱格式不正确") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "邮箱格式不正确") String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public @NotBlank(message = "医院名称不能为空") String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(@NotBlank(message = "医院名称不能为空") String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public @NotBlank(message = "科室不能为空") String getDepartment() {
        return department;
    }

    public void setDepartment(@NotBlank(message = "科室不能为空") String department) {
        this.department = department;
    }

    public @NotBlank(message = "职称不能为空") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "职称不能为空") String title) {
        this.title = title;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
