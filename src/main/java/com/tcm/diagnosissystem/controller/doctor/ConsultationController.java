package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.dto.request.doctor.ConsultationCreateRequest;
import com.tcm.diagnosissystem.dto.response.doctor_patient.ConsultationResponse;
import com.tcm.diagnosissystem.service.doctor.consultation.ConsultationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问诊记录管理
 */
@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 创建问诊记录
     */
    @PostMapping
    public Result<ConsultationResponse> create(
            HttpServletRequest servletRequest,
            @Validated @RequestBody ConsultationCreateRequest request) {

        Long userId = (Long) servletRequest.getAttribute("userId");

        if (userId == null) {
            return Result.failed("未登录");
        }

        try {
            ConsultationResponse response = consultationService.create(userId, request);
            return Result.success("创建成功", response);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取问诊记录详情
     */
    @GetMapping("/{id}")
    public Result<ConsultationResponse> getById(
            HttpServletRequest servletRequest,
            @PathVariable Long id) {

        Long userId = (Long) servletRequest.getAttribute("userId");

        if (userId == null) {
            return Result.failed("未登录");
        }

        try {
            ConsultationResponse response = consultationService.getById(id, userId);
            return Result.success(response);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取我的所有问诊记录
     */
    @GetMapping("/my")
    public Result<List<ConsultationResponse>> getMyConsultations(HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");

        if (userId == null) {
            return Result.failed("未登录");
        }

        try {
            List<ConsultationResponse> list = consultationService.getMyConsultations(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 分页获取我的问诊记录
     */
    @GetMapping("/my/page")
    public Result<List<ConsultationResponse>> getMyConsultationsWithPage(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = (Long) servletRequest.getAttribute("userId");

        if (userId == null) {
            return Result.failed("未登录");
        }

        try {
            List<ConsultationResponse> list = consultationService.getMyConsultationsWithPage(userId, page, size);
            return Result.success(list);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 删除问诊记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            HttpServletRequest servletRequest,
            @PathVariable Long id) {

        Long userId = (Long) servletRequest.getAttribute("userId");

        if (userId == null) {
            return Result.failed("未登录");
        }

        try {
            consultationService.delete(id, userId);
            return Result.success("删除成功",null);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
}
