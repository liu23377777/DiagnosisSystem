package com.tcm.diagnosissystem.controller.patient;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.dto.response.doctor.DiagnosisComparisonResponse;
import com.tcm.diagnosissystem.dto.response.doctor_patient.DiagnosisStatisticsResponse;
import com.tcm.diagnosissystem.dto.response.doctor.HealthTrendResponse;
import com.tcm.diagnosissystem.entity.TongueDiagnosis;
import com.tcm.diagnosissystem.service.doctor_patient.diagnosis.TongueDiagnosisService;
import com.tcm.diagnosissystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tongue")
public class TongueController {

    private static final Logger log = LoggerFactory.getLogger(TongueController.class);
    @Autowired private TongueDiagnosisService tongueService;
    @Autowired private JwtUtil jwtUtil;

    /* 公共解析 userId：token -> request attribute */
    private Long resolveUserId(String token, HttpServletRequest req){
        if(token!=null && token.startsWith("Bearer ")){
            try { return jwtUtil.getUserIdFromToken(token.substring(7)); } catch (Exception ignored) {}
        }
        Object attr=req.getAttribute("userId");
        return attr instanceof Long ? (Long)attr : null;
    }

    /** 上传舌诊图片进行分析 (token 可选) */
    @PostMapping("/analyze")
    public Result<TongueDiagnosis> analyzeTongue(
            @RequestHeader(value="Authorization", required = false) String token,
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request) {
        try {
            Long userId = resolveUserId(token, request);
            if(userId==null) return Result.failed("未登录，无法分析");
            TongueDiagnosis result = tongueService.analyzeTongue(userId, image);
            return Result.success("舌诊分析完成", result);
        } catch (Exception e) {
            log.error("舌诊分析失败", e);
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("/history")
    public Result<List<TongueDiagnosis>> getHistory(@RequestHeader(value="Authorization", required=false) String token,
                                                    HttpServletRequest request){
        try {
            Long userId=resolveUserId(token,request);
            if(userId==null) return Result.failed("未登录");
            return Result.success(tongueService.getUserHistory(userId));
        }catch(Exception e){log.error("历史查询失败",e);return Result.failed(e.getMessage());}
    }

    /* 其余接口同样改为 optional token → resolveUserId */
    @GetMapping("/trend")
    public Result<HealthTrendResponse> getHealthTrend(@RequestHeader(value="Authorization",required=false) String token,
                                                      @RequestParam(defaultValue="7") int days,
                                                      HttpServletRequest request){
        try{
            Long userId=resolveUserId(token,request);
            if(userId==null) return Result.failed("未登录");
            return Result.success(tongueService.analyzeHealthTrend(userId,days));
        }catch(Exception e){return Result.failed(e.getMessage());}
    }

    @GetMapping("/compare")
    public Result<DiagnosisComparisonResponse> compare(@RequestHeader(value="Authorization",required=false) String token,
                                                       @RequestParam Long oldId,@RequestParam Long newId,
                                                       HttpServletRequest request){
        Long userId=resolveUserId(token,request);
        if(userId==null) return Result.failed("未登录");
        return Result.success(tongueService.compareDiagnosis(oldId,newId,userId));
    }

    @GetMapping("/statistics")
    public Result<DiagnosisStatisticsResponse> stats(@RequestHeader(value="Authorization",required=false) String token,
                                                     HttpServletRequest request){
        Long userId=resolveUserId(token,request);
        if(userId==null) return Result.failed("未登录");
        return Result.success(tongueService.getUserStatistics(userId));
    }

    /* 不需要登录的接口保持不变 */
    @GetMapping("/detail/{id}")
    public Result<TongueDiagnosis> detail(@PathVariable Long id){
        return Result.success(tongueService.getDetail(id));
    }

    @GetMapping("/syndrome/{syndrome}")
    public Result<List<TongueDiagnosis>> bySyndrome(@PathVariable String syndrome){
        return Result.success(tongueService.findBySyndrome(syndrome));
    }
}
