package com.tcm.diagnosissystem.service;

import com.tcm.diagnosissystem.dto.request.doctor.DoctorRegisterRequest;
import com.tcm.diagnosissystem.dto.request.login.LoginRequest;
import com.tcm.diagnosissystem.dto.request.patient.PatientRegisterRequest;
import com.tcm.diagnosissystem.dto.response.login.LoginResponse;
import com.tcm.diagnosissystem.dto.response.admin_patient_doctor.UserInfo;
import com.tcm.diagnosissystem.entity.admin.Admin;
import com.tcm.diagnosissystem.entity.doctor.Doctor;
import com.tcm.diagnosissystem.entity.patient.Patient;
import com.tcm.diagnosissystem.mapper.admin.AdminMapper;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import com.tcm.diagnosissystem.mapper.patient.PatientMapper;
import com.tcm.diagnosissystem.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired private PatientMapper patientMapper;
    @Autowired private DoctorMapper  doctorMapper;
    @Autowired private AdminMapper   adminMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // ========= 医生注册 =========
    public UserInfo registerDoctor(DoctorRegisterRequest req){
        if(!req.getPassword().equals(req.getConfirmPassword())) throw new RuntimeException("两次密码不一致");
        if(doctorMapper.selectByUsername(req.getUsername())!=null) throw new RuntimeException("用户名已存在");

        Doctor d = new Doctor();
        d.setUsername(req.getUsername());
        d.setPassword(passwordEncoder.encode(req.getPassword()));
        d.setRealName(req.getRealName());
        d.setPhone(req.getPhone());
        d.setEmail(req.getEmail());
        d.setGender(req.getGender());
        d.setHospitalName(req.getHospitalName());
        d.setDepartment(req.getDepartment());
        d.setTitle(req.getTitle());
        d.setSpecialty(req.getSpecialty());
        d.setStatus(0); // 待审核
        LocalDateTime now = LocalDateTime.now();
        d.setCreateTime(now); d.setUpdateTime(now);
        doctorMapper.insert(d);

        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(d, info);
        info.setRole("DOCTOR");
        info.setStatus(0);
        return info;
    }

    // ========= 患者注册 =========
    public UserInfo registerPatient(PatientRegisterRequest req){
        if(!req.getPassword().equals(req.getConfirmPassword())) throw new RuntimeException("两次密码不一致");
        if(patientMapper.selectByUsername(req.getUsername())!=null) throw new RuntimeException("用户名已存在");
        if(patientMapper.selectByPhone(req.getPhone())!=null) throw new RuntimeException("手机号已存在");
        Patient p=new Patient();
        p.setUsername(req.getUsername());
        p.setPassword(passwordEncoder.encode(req.getPassword()));
        p.setRealName(req.getRealName());
        p.setPhone(req.getPhone());
        p.setEmail(req.getEmail());
        p.setGender(req.getGender());
        p.setStatus(1);
        p.setCreateTime(LocalDateTime.now());p.setUpdateTime(LocalDateTime.now());
        patientMapper.insert(p);
        UserInfo ui=new UserInfo();
        BeanUtils.copyProperties(p,ui);ui.setRole("PATIENT");
        return ui;
    }

    // ========= 登录 =========
    public LoginResponse loginAndSetCookie(LoginRequest req, HttpServletResponse response) {
        String username = req.getUsername();
        String password = req.getPassword();
        String frontRole = req.getRole();   // ① 取前端传来的角色
    
        Long userId = null;
        String role = null;
        String encodedPwd = null;
        String realName = null;
    
        // ----- 患者 -----
        Patient pt = patientMapper.selectByUsername(username);
        if (pt != null) {
            if (pt.getStatus() == null || pt.getStatus() != 1) {
                throw new RuntimeException("患者账号已被禁用或待审核");
            }
            userId = pt.getId();
            role = "PATIENT";
            encodedPwd = pt.getPassword();
            realName = pt.getRealName();
        }
    
        // ----- 医生 -----
        if (userId == null) {
            Doctor dc = doctorMapper.selectByUsername(username);
            if (dc != null) {
                if (dc.getStatus() == null || dc.getStatus() != 1) {
                    throw new RuntimeException("医生账号未审核或已被禁用");
                }
                userId = dc.getId();
                role = "DOCTOR";
                encodedPwd = dc.getPassword();
                realName = dc.getRealName();
            }
        }
    
        // ----- 管理员 -----
        if (userId == null) {
            Admin ad = adminMapper.selectByUsername(username);
            if (ad != null) {
                userId = ad.getId();
                role = "ADMIN";
                encodedPwd = ad.getPassword();
                realName = ad.getRealName();
            }
        }
    
        // 账号不存在
        if (userId == null) throw new RuntimeException("用户不存在");
    
        // 身份不匹配校验 ②
        if (frontRole != null && !frontRole.isBlank() && !frontRole.equalsIgnoreCase(role)) {
            throw new RuntimeException("所选身份与账号类型不匹配");
        }
    
        // 密码校验
        if (encodedPwd == null || !passwordEncoder.matches(password, encodedPwd.trim())) {
            throw new RuntimeException("密码错误");
        }
    
        // 生成 JWT & 写 Cookie
        String jwt = jwtUtil.generateToken(userId, role);
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
    
        UserInfo info = UserInfo.builder()
                .id(userId)
                .username(username)
                .realName(realName)
                .role(role)
                .build();
    
        LoginResponse resp = new LoginResponse();
        resp.setToken(jwt);
        resp.setRole(role);
        resp.setUserInfo(info);
        return resp;
    }
}