package com.tcm.diagnosissystem.service.doctor;

import com.tcm.diagnosissystem.entity.Appointment;
import com.tcm.diagnosissystem.entity.Payment;
import com.tcm.diagnosissystem.mapper.doctor.AppointmentMapper;
import com.tcm.diagnosissystem.mapper.doctor.NonDrugCatalogMapper;
import com.tcm.diagnosissystem.mapper.doctor.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private NonDrugCatalogMapper catalogMapper;

    /**
     * 问诊结束后创建待支付记录
     * @param appointmentId 预约 id
     * @param itemName      费用项目名称（non_drug_catalog_sql_results.item_name）
     */
    @Transactional
    public Payment createPaymentForAppointment(Long appointmentId, String itemName) {
        // 若已生成则直接返回
        Payment existing = paymentMapper.findByAppointmentId(appointmentId);
        if (existing != null) {
            return existing;
        }
        // 检查预约
        Appointment appt = appointmentMapper.findById(appointmentId);
        if (appt == null) {
            throw new RuntimeException("预约记录不存在");
        }
        // 查单价
        BigDecimal price = catalogMapper.findPriceByItemName(itemName);
        if (price == null) {
            // 若项目不存在，给一个默认值，防止空指针
            price = new BigDecimal("10.00");
        }
        Payment p = new Payment();
        p.setAppointmentId(appointmentId);
        p.setPatientId(appt.getPatientId());
        p.setDoctorId(appt.getDoctorId());
        p.setItemName(itemName);
        p.setAmount(price);
        p.setStatus(0);
        paymentMapper.insert(p);
        return p;
    }

    @Transactional
    public void completePayment(Long paymentId) {
        Payment p = paymentMapper.findById(paymentId);
        if (p == null) throw new RuntimeException("支付记录不存在");
        if (p.getStatus() == 1) throw new RuntimeException("该笔订单已支付");
        paymentMapper.updateStatus(paymentId, 1);
    }

    public Payment getPaymentById(Long id) { return paymentMapper.findById(id); }

    public List<Payment> getPaymentsByPatient(Long patientId) { return paymentMapper.findByPatientId(patientId); }
}
