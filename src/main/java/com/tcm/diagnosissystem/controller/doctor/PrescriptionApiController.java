package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.dto.request.doctor.PrescriptionCreateRequest;
import com.tcm.diagnosissystem.entity.Prescription;
import com.tcm.diagnosissystem.entity.PrescriptionItem;
import com.tcm.diagnosissystem.service.doctor.PrescriptionService;
import com.tcm.diagnosissystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionApiController {

    @Autowired private PrescriptionService prescriptionService;
    @Autowired private JwtUtil jwtUtil;

        @GetMapping("/my")
    public Result<?> my(@RequestHeader("Authorization") String auth){
        Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ",""));
        List<Prescription> list = prescriptionService.listByDoctor(doctorId);
        List<Map<String,Object>> resp = new java.util.ArrayList<>();
        for(Prescription p: list){
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", p.getId());
            m.put("patientId", p.getPatientId());
            m.put("totalPrice", p.getTotalAmount());
            m.put("createTime", p.getCreateTime());
            resp.add(m);
        }
        return Result.success(resp);
    }

        /**
     * 处方明细接口，为前端字段名做转换
     */
    @GetMapping("/{id}/items")
    public Result<?> items(@PathVariable Long id){
        List<PrescriptionItem> list = prescriptionService.getItems(id);
        List<Map<String,Object>> resp = new java.util.ArrayList<>();
        for(PrescriptionItem it : list){
            Map<String,Object> m = new HashMap<>();
            m.put("drugName", it.getItemName());
            m.put("quantity", it.getQuantity());
            m.put("unitPrice", it.getUnitPrice());
            m.put("dosage", it.getUsage());
            resp.add(m);
        }
        return Result.success(resp);
    }

    @PostMapping
    public Result<?> create(@RequestHeader("Authorization") String auth,
                            @RequestBody PrescriptionCreateRequest req){
        Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ",""));
        Prescription p = prescriptionService.create(doctorId, req);
        Map<String,Object> resp = new HashMap<>();
        resp.put("prescription", p);
        resp.put("items", prescriptionService.getItems(p.getId()));
        return Result.success("处方已创建", resp);
    }
}

