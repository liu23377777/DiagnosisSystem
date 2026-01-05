package com.tcm.diagnosissystem.dto.response.admin_patient_doctor;

public class UserInfo {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer gender;
    private String role;
    private Integer status;

    // 医生专属字段
    private String hospital;
    private String department;
    private String title;

    // 默认构造函数
    public UserInfo() {}

    // Builder 方法
    public static UserInfoBuilder builder() {
        return new UserInfoBuilder();
    }

    // Builder 类
    public static class UserInfoBuilder {
        private Long id;
        private String username;
        private String realName;
        private String phone;
        private String email;
        private Integer gender;
        private String role;
        private Integer status;
        private String hospital;
        private String department;
        private String title;

        public UserInfoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserInfoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserInfoBuilder realName(String realName) {
            this.realName = realName;
            return this;
        }

        public UserInfoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserInfoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserInfoBuilder gender(Integer gender) {
            this.gender = gender;
            return this;
        }

        public UserInfoBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserInfoBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public UserInfoBuilder hospital(String hospital) {
            this.hospital = hospital;
            return this;
        }

        public UserInfoBuilder department(String department) {
            this.department = department;
            return this;
        }

        public UserInfoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public UserInfo build() {
            UserInfo info = new UserInfo();
            info.setId(id);
            info.setUsername(username);
            info.setRealName(realName);
            info.setPhone(phone);
            info.setEmail(email);
            info.setGender(gender);
            info.setRole(role);
            info.setStatus(status);
            info.setHospital(hospital);
            info.setDepartment(department);
            info.setTitle(title);
            return info;
        }
    }

    // ===== Getters and Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
