// 路径: src/main/java/com/tcm/diagnosissystem/controller/doctor/TongueReviewController.java
package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.dto.request.doctor.TongueReviewRequest;
import com.tcm.diagnosissystem.dto.response.doctor_patient.TongueDetailResponse;
import com.tcm.diagnosissystem.service.doctor.TongueReviewService;
import com.tcm.diagnosissystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/tongue-review")
public class TongueReviewController {

    @Autowired
    private TongueReviewService tongueReviewService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取待审核的舌诊列表
     */
    @GetMapping("/pending")
    public Result<List<TongueDetailResponse>> getPendingReviews() {
        try {
            List<TongueDetailResponse> list = tongueReviewService.getPendingReviews();
            return Result.success(list);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取我已审核的舌诊记录
     */
    @GetMapping("/my-reviews")
    public Result<List<TongueDetailResponse>> getMyReviews(@RequestHeader("Authorization") String token) {
        try {
            Long doctorId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            List<TongueDetailResponse> list = tongueReviewService.getReviewedByDoctor(doctorId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取舌诊详情
     */
    @GetMapping("/detail/{tongueId}")
    public Result<TongueDetailResponse> getTongueDetail(@PathVariable Long tongueId) {
        try {
            TongueDetailResponse response = tongueReviewService.getTongueDetail(tongueId);
            return Result.success(response);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 提交审核
     */
    @PostMapping("/submit")
    public Result<TongueDetailResponse> submitReview(
            @RequestHeader("Authorization") String token,
            @RequestBody TongueReviewRequest request) {

        try {
            Long doctorId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            TongueDetailResponse response = tongueReviewService.reviewTongue(doctorId, request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
}
