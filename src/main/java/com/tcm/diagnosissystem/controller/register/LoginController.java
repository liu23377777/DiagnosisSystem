package com.tcm.diagnosissystem.controller.register;

import com.tcm.diagnosissystem.dto.request.doctor.DoctorRegisterRequest;
import com.tcm.diagnosissystem.dto.request.patient.PatientRegisterRequest;
import com.tcm.diagnosissystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired private AuthService authService;

    @GetMapping("/login")
    public String login() {return "login";}

    @GetMapping("/register")
    public String register(){return "register";}

    @PostMapping("/register")
    public String doRegister(@RequestParam String role,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             @RequestParam String realName,
                             @RequestParam String phone,
                             @RequestParam String email,
                             @RequestParam Integer gender,
                             @RequestParam(required = false) String hospitalName,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String specialty,
                             RedirectAttributes ra, Model model){
        try{
            if("DOCTOR".equals(role)){
                DoctorRegisterRequest r=new DoctorRegisterRequest();
                r.setUsername(username);r.setPassword(password);r.setConfirmPassword(confirmPassword);
                r.setRealName(realName);r.setPhone(phone);r.setEmail(email);r.setGender(gender);
                r.setHospitalName(hospitalName);r.setDepartment(department);r.setTitle(title);r.setSpecialty(specialty);
                authService.registerDoctor(r);
                ra.addFlashAttribute("success","医生注册已提交，等待管理员审核");
            }else{
                PatientRegisterRequest r=new PatientRegisterRequest();
                r.setUsername(username);r.setPassword(password);r.setConfirmPassword(confirmPassword);
                r.setRealName(realName);r.setPhone(phone);r.setEmail(email);r.setGender(gender);
                authService.registerPatient(r);
                ra.addFlashAttribute("success","注册成功，请登录");
            }
            return "redirect:/login";
        }catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "register";
        }
    }

    @GetMapping("/")
    public String home(){return "redirect:/login";}
}
