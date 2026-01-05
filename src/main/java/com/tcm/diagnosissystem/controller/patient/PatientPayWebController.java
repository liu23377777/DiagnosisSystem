package com.tcm.diagnosissystem.controller.patient;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 患者支付页面
 */
@Controller
@RequestMapping("/patient")
public class PatientPayWebController {

    @GetMapping("/pay/{paymentId}")
    public String pay(@PathVariable Long paymentId, Model model){
        model.addAttribute("paymentId", paymentId);
        return "patient/pay";
    }
}

