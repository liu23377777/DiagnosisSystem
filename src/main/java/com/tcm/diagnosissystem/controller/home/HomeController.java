package com.tcm.diagnosissystem.controller.home;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String redirectToRoleHome(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        if (role == null) {
            return "redirect:/login";
        }

        return switch (role) {
            case "PATIENT" -> "redirect:/patient/home";
            case "DOCTOR" -> "redirect:/doctor/home";
            case "ADMIN" -> "redirect:/admin/dashboard";
            default -> "redirect:/login";
        };
    }
}
