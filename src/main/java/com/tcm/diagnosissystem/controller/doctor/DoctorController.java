package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.entity.Doctor;
import com.tcm.diagnosissystem.service.doctor.DoctorService;
import com.tcm.diagnosissystem.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired private DoctorService doctorService;
    @Autowired private JwtUtil jwtUtil;

    /**
     * 解析 userId：任何 token 异常都吞掉，避免因旧 token 直接 500。
     */
    private Long resolveUserId(HttpServletRequest request){
        // 1) request attribute
        Object attr = request.getAttribute("userId");
        if (attr instanceof Long) return (Long) attr;

        // 2) Authorization header
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String v = auth.substring(7);
            try {
                // 角色校验：必须是 DOCTOR
                String role = jwtUtil.getRoleFromToken(v);
                if (!"DOCTOR".equals(role)) {
                    return null;
                }
                return jwtUtil.getUserIdFromToken(v);
            } catch (Exception ignored) {
                return null;
            }
        }

        // 3) token cookie
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) {
                    String v = c.getValue();
                    // 兼容历史写入的 Bearer 前缀
                    while (v != null && v.startsWith("Bearer ")) {
                        v = v.substring(7);
                    }
                    try {
                        // 角色校验：必须是 DOCTOR
                        String role = jwtUtil.getRoleFromToken(v);
                        if (!"DOCTOR".equals(role)) {
                            return null;
                        }
                        return jwtUtil.getUserIdFromToken(v);
                    } catch (Exception ignored) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        Doctor doctor = doctorService.getDoctorById(userId);
        if (doctor == null) {
            return "redirect:/login";
        }

        model.addAttribute("userId", userId);
        model.addAttribute("doctorName", doctor.getRealName());
        return "doctor/home";
    }

    @GetMapping("/pending-diagnosis")
    public String pendingDiagnosisPage(HttpServletRequest request, Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("userId", userId);
        return "doctor/pending-diagnosis";
    }

    @GetMapping("/diagnosis-detail")
    public String diagnosisDetailPage(@RequestParam("id") Long diagnosisId,
                                      HttpServletRequest request,
                                      Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("userId", userId);
        model.addAttribute("diagnosisId", diagnosisId);
        return "doctor/diagnosis-detail";
    }

    /** 舌诊审核页面 */
    @GetMapping("/tongue-review")
    public String tongueReview(HttpServletRequest request, Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("doctorId", userId);
        return "doctor/tongue-review";
    }

    /** 处方管理页面 */
    @GetMapping("/prescriptions")
    public String prescriptions(HttpServletRequest request, Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("doctorId", userId);
        return "doctor/prescriptions";
    }

    /** 问诊记录管理页面 */
    @GetMapping("/consultations")
    public String consultations(HttpServletRequest request, Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("doctorId", userId);
        return "doctor/consultations";
    }

    /** 医生个人设置页面 */
    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("doctorId", userId);
        return "doctor/profile";
    }
}
