package com.tcm.diagnosissystem.controller.patient;

import com.tcm.diagnosissystem.entity.Patient;
import com.tcm.diagnosissystem.entity.TongueDiagnosis;
import com.tcm.diagnosissystem.service.patient.PatientService;
import com.tcm.diagnosissystem.service.doctor_patient.diagnosis.TongueDiagnosisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 患者页面路由控制器
 */
@Controller
@RequestMapping("/patient")
public class PatientWebController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private TongueDiagnosisService diagnosisService;

    /**
     * 患者主页
     */
    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            Patient patient = patientService.getPatientById(userId);
            model.addAttribute("patient", patient);
            model.addAttribute("userId", userId);
        }
        return "patient/patient-home";
    }

    /**
     * 舌诊分析页面
     */
    @GetMapping("/diagnosis")
    public String diagnosis(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            Patient patient = patientService.getPatientById(userId);
            model.addAttribute("userId", userId);
            model.addAttribute("patient", patient);
        }
        return "patient/tongue-test";
    }

    /**
     * 诊断记录页面
     */
    @GetMapping("/records")
    public String records(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("userId", userId);
        }
        return "patient/diagnosis-records";
    }

    /**
     * 舌诊详情页面
     */
    @GetMapping("/diagnosis/detail/{id}")
    public String diagnosisDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 查询诊断详情
        TongueDiagnosis diagnosis = diagnosisService.getDiagnosisById(id);

        // 如果已登录则校验是否本人记录
        if (userId != null && !diagnosis.getUserId().equals(userId)) {
            return "error/403";
        }

        model.addAttribute("diagnosis", diagnosis);
        model.addAttribute("userId", userId);
        return "patient/diagnosis-detail";
    }

    /**
     * 舌诊历史页面
     */
    @GetMapping("/appointment")
    public String appointment(){
        return "patient/appointment";
    }

    @GetMapping("/history")
    public String history(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        model.addAttribute("userId", userId);
        return "patient/history";
    }

    /**
     * 个人中心页面
     */
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            Patient patient = patientService.getPatientById(userId);
            model.addAttribute("patient", patient);
        }
        return "patient/profile";
    }
}
