package com.tcm.diagnosissystem.controller.login;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.dto.request.doctor.DoctorRegisterRequest;
import com.tcm.diagnosissystem.dto.request.login.LoginRequest;
import com.tcm.diagnosissystem.dto.request.patient.PatientRegisterRequest;
import com.tcm.diagnosissystem.dto.response.login.LoginResponse;
import com.tcm.diagnosissystem.dto.response.admin_patient_doctor.UserInfo;
import com.tcm.diagnosissystem.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                       HttpServletResponse response) {
        try {
            LoginResponse resp = authService.loginAndSetCookie(loginRequest, response);
            return Result.success(resp);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("/register/patient")
    public Result<UserInfo> registerPatient(@RequestBody PatientRegisterRequest req){
        try {
            return Result.success("注册成功", authService.registerPatient(req));
        }catch (Exception e){
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("/register/doctor")
    public Result<UserInfo> registerDoctor(@RequestBody DoctorRegisterRequest req){
        try {
            return Result.success("提交成功，等待管理员审核", authService.registerDoctor(req));
        }catch (Exception e){
            return Result.failed(e.getMessage());
        }
    }
}
