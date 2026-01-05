package com.tcm.diagnosissystem.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 管理员页面路由控制器
 */
@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping({"/dashboard","/home"})
    public String dashboard(){
        return "admin/dashboard";
    }

    @GetMapping("/patients")
    public String patients(){
        return "admin/patients";
    }

    @GetMapping("/doctors")
    public String doctors(){
        return "admin/doctors";
    }

    @GetMapping("/stats")
    public String stats(){
        return "admin/stats";
    }

    @GetMapping("/profile")
    public String profile(){
        return "admin/profile";
    }
}
