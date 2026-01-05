package com.tcm.diagnosissystem.dto.response.login;

import com.tcm.diagnosissystem.dto.response.admin_patient_doctor.UserInfo;

/**
 * 登录成功返回 DTO
 */
public class LoginResponse {
    private String token;
    private String role;           // PATIENT / DOCTOR / ADMIN
    private UserInfo userInfo;

    public LoginResponse() {}

    public LoginResponse(String token, String role, UserInfo userInfo) {
        this.token = token;
        this.role = role;
        this.userInfo = userInfo;
    }

    /* ---------------- builder ---------------- */
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String token;
        private String role;
        private UserInfo userInfo;
        public Builder token(String t){this.token=t; return this;}
        public Builder role(String r){this.role=r; return this;}
        public Builder userInfo(UserInfo u){this.userInfo=u; return this;}
        public LoginResponse build(){ return new LoginResponse(token,role,userInfo);} }

    /* -------------- getter / setter -------------- */
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }
}
