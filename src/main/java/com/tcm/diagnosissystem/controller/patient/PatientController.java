package com.tcm.diagnosissystem.controller.patient;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.dto.response.patient.PatientInfoResponse;
import com.tcm.diagnosissystem.entity.Patient;
import com.tcm.diagnosissystem.mapper.patient.PatientMapper;
import com.tcm.diagnosissystem.service.patient.PatientService;
import com.tcm.diagnosissystem.service.doctor_patient.diagnosis.TongueDiagnosisService;
import com.tcm.diagnosissystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 患者控制器
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private TongueDiagnosisService tongueDiagnosisService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PatientMapper patientMapper;

    private static final String UPLOAD_DIR = "uploads/avatar/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB


    /**
     * 获取当前登录患者信息
     */
    @GetMapping("/info")
    public Result<PatientInfoResponse> getPatientInfo(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request) {

        Long userId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        if (userId == null) {
            userId = (Long) request.getAttribute("userId");
        }
        if (userId == null) {
            return Result.failed("未登录");
        }

        Patient patient = patientService.getPatientById(userId);
        if (patient == null) {
            return Result.failed("患者不存在");
        }

        PatientInfoResponse response = new PatientInfoResponse();
        BeanUtils.copyProperties(patient, response);
        response.setPassword(null); // 不返回密码

        return Result.success(response);
    }

    /**
     * 修改患者信息
     */
    @PutMapping("/info")
    public Result<PatientInfoResponse> updatePatientInfo(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request,
            @RequestBody PatientInfoResponse updateRequest) {

        Long userId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        if (userId == null) {
            userId = (Long) request.getAttribute("userId");
        }
        if (userId == null) {
            return Result.failed("未登录");
        }

        Patient patient = patientService.getPatientById(userId);
        if (patient == null) {
            return Result.failed("患者不存在");
        }

        // 更新允许修改的字段
        if (updateRequest.getRealName() != null) {
            patient.setRealName(updateRequest.getRealName());
        }
        if (updateRequest.getPhone() != null) {
            patient.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getEmail() != null) {
            patient.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getGender() != null) {
            patient.setGender(updateRequest.getGender());
        }
        if (updateRequest.getBirthDate() != null) {
            patient.setBirthDate(updateRequest.getBirthDate());
        }
        if (updateRequest.getAddress() != null) {
            patient.setAddress(updateRequest.getAddress());
        }

        patientService.updatePatient(patient);

        PatientInfoResponse response = new PatientInfoResponse();
        BeanUtils.copyProperties(patient, response);
        response.setPassword(null);

        return Result.success("修改成功", response);
    }

    /**
     * 获取患者诊断记录统计
     */
    @GetMapping("/records")
    public Result<?> getDiagnosisRecords(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request) {

        Long userId = null;

        // 1) 优先从 JWT token 解析（前端页面是用 localStorage token 调用接口的）
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }

        // 2) 兜底：从过滤器放进来的 request attribute 获取
        if (userId == null) {
            userId = (Long) request.getAttribute("userId");
        }

        if (userId == null) {
            return Result.failed("未登录");
        }

        // 3) 返回真实诊断记录（舌诊历史）
        return Result.success(tongueDiagnosisService.getUserHistory(userId));
    }

    /**
     * 获取患者信息页面数据
     */
    @GetMapping("/info-page")
    public Result<?> getPatientInfoPage(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.failed("未登录");
        }

        Patient patient = patientService.getPatientById(userId);
        if (patient == null) {
            return Result.failed("患者不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("patient", patient);
        data.put("diagnosisCount", 0); // 需要从舌诊记录表查询
        data.put("lastDiagnosisTime", null); // 最后诊断时间

        return Result.success(data);
    }


    /**
     * 上传患者头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
        
        Long userId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        if (userId == null) {
            userId = (Long) request.getAttribute("userId");
        }
        if (userId == null) {
            return Result.failed("未登录");
        }

        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return Result.failed("请选择要上传的图片");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.failed("只支持图片格式（jpg、png、jpeg等）");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.failed("图片大小不能超过5MB");
            }

            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Files.write(filePath, file.getBytes());

            // 更新数据库
            String avatarUrl = "/uploads/avatar/" + filename;
            patientMapper.updateAvatarUrl(userId, avatarUrl, LocalDateTime.now());

            return Result.success("头像上传成功", avatarUrl);
        } catch (IOException e) {
            return Result.failed("文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.failed("上传失败: " + e.getMessage());
        }
    }
}
