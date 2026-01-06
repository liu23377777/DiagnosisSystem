package com.tcm.diagnosissystem.service.doctor;

import com.tcm.diagnosissystem.dto.request.doctor.PrescriptionCreateRequest;
import com.tcm.diagnosissystem.dto.response.doctor.PrescriptionResponse;
import com.tcm.diagnosissystem.entity.doctor.Prescription;
import com.tcm.diagnosissystem.entity.PrescriptionItem;
import com.tcm.diagnosissystem.mapper.doctor.PrescriptionItemMapper;
import com.tcm.diagnosissystem.mapper.doctor.PrescriptionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PrescriptionService {

    @Autowired private PrescriptionMapper prescriptionMapper;
    @Autowired private PrescriptionItemMapper prescriptionItemMapper;

    @Transactional
    public PrescriptionResponse createPrescription(Long doctorId, PrescriptionCreateRequest req){
        Prescription p = create(doctorId, req);
        PrescriptionResponse resp = buildResponse(p);
        resp.setItems(convertItems(prescriptionItemMapper.selectByPrescriptionId(p.getId())));
        return resp;
    }

    public List<PrescriptionResponse> getDoctorPrescriptions(Long doctorId){
        List<Prescription> list = prescriptionMapper.selectByDoctorId(doctorId);
        List<PrescriptionResponse> res = new ArrayList<>();
        for(Prescription p: list){
            res.add(buildResponse(p));
        }
        return res;
    }

    public PrescriptionResponse getPrescriptionDetail(Long id){
        Prescription p = prescriptionMapper.selectById(id);
        if(p==null) throw new RuntimeException("处方不存在");
        PrescriptionResponse resp = buildResponse(p);
        resp.setItems(convertItems(prescriptionItemMapper.selectByPrescriptionId(id)));
        return resp;
    }

    public List<Prescription> listByDoctor(Long doctorId){
        return prescriptionMapper.selectByDoctorId(doctorId);
    }

    public List<PrescriptionItem> getItems(Long prescriptionId){
        return prescriptionItemMapper.selectByPrescriptionId(prescriptionId);
    }

    @Transactional
    public Prescription create(Long doctorId, PrescriptionCreateRequest req){
        if(req.getPatientId()==null) throw new RuntimeException("patientId不能为空");
        if(req.getItems()==null || req.getItems().isEmpty()) throw new RuntimeException("处方明细不能为空");

        BigDecimal total = BigDecimal.ZERO;
        List<PrescriptionItem> items = new ArrayList<>();

        for(PrescriptionCreateRequest.Item i : req.getItems()){
            if(i.getQuantity()==null || i.getQuantity()<=0) throw new RuntimeException("数量必须>0");
            BigDecimal unit = i.getUnitPrice() != null ? i.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal sub = unit.multiply(BigDecimal.valueOf(i.getQuantity()));
            total = total.add(sub);

            PrescriptionItem pi = new PrescriptionItem();
            pi.setItemType("drug");
            pi.setItemCode(i.getDrugCode());
            pi.setItemName(i.getDrugName());
            pi.setSpecification(null);
            pi.setUnitPrice(unit);
            pi.setQuantity(i.getQuantity());
            pi.setSubtotal(sub);
            pi.setUsage(i.getDosage());
            pi.setFrequency(null);
            items.add(pi);
        }

        Prescription p = new Prescription();
        p.setDoctorId(doctorId);
        p.setPatientId(req.getPatientId());
        p.setDiagnosisId(req.getDiagnosisId());
        p.setTotalAmount(total);
        p.setPrescriptionType("drug");
        p.setStatus("pending");
        p.setPrescriptionNo(generatePrescriptionNo());
        p.setCreateTime(LocalDateTime.now());
        p.setUpdateTime(LocalDateTime.now());

        prescriptionMapper.insert(p);

        for(PrescriptionItem pi: items){
            pi.setPrescriptionId(p.getId());
        }
        prescriptionItemMapper.insertBatch(items);

        return p;
    }

    private String generatePrescriptionNo(){
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "RX" + ts + rand;
    }

    private PrescriptionResponse buildResponse(Prescription p){
        PrescriptionResponse resp = new PrescriptionResponse();
        BeanUtils.copyProperties(p, resp);
        // PrescriptionResponse 里是 totalPrice 字段的话，需要后续同步 DTO；现在先返回基础字段
        return resp;
    }

    private List<PrescriptionResponse.Item> convertItems(List<PrescriptionItem> items){
        List<PrescriptionResponse.Item> list = new ArrayList<>();
        if(items==null) return list;
        for(PrescriptionItem pi: items){
            PrescriptionResponse.Item it = new PrescriptionResponse.Item();
            it.setDrugCode(pi.getItemCode());
            it.setDrugName(pi.getItemName());
            it.setDosage(pi.getUsage());
            it.setQuantity(pi.getQuantity());
            it.setUnitPrice(pi.getUnitPrice());
            list.add(it);
        }
        return list;
    }
}
