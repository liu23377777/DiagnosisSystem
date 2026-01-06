package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.patient.Payment;
import com.tcm.diagnosissystem.service.doctor.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 医生结束问诊，为预约创建支付单
     * @param appointmentId 预约ID
     * @param itemName      非药品项目名称
     */
    @PostMapping("/create-from-appointment/{appointmentId}")
    public Result<Payment> createPayment(@PathVariable Long appointmentId,
                                         @RequestParam String itemName) {
        try {
            Payment payment = paymentService.createPaymentForAppointment(appointmentId, itemName);
            return Result.success("支付单已生成", payment);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 患者模拟支付成功
     */
    @PostMapping("/complete/{paymentId}")
    public Result<?> completePayment(@PathVariable Long paymentId) {
        try {
            paymentService.completePayment(paymentId);
            return Result.success("支付成功");
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Payment> getPayment(@PathVariable Long id) {
        Payment pay = paymentService.getPaymentById(id);
        if (pay == null) return Result.failed("支付单不存在");
        return Result.success(pay);
    }
}
