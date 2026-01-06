package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.doctor.Doctor;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import com.tcm.diagnosissystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/doctor")
public class DoctorApiController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前登录医生的信息
     */
    @GetMapping("/info")
    public Result<Doctor> getDoctorInfo(@RequestHeader("Authorization") String auth) {
        try {
            Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ", ""));
            Doctor doctor = doctorMapper.selectById(doctorId);
            if (doctor != null) {
                doctor.setPassword(null); // 不返回密码
                return Result.success(doctor);
            }
            return Result.failed("医生信息不存在");
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 更新当前登录医生的信息
     */
    @PutMapping("/info")
    public Result<String> updateDoctorInfo(@RequestHeader("Authorization") String auth, @RequestBody Doctor doctor) {
        try {
            Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ", ""));
            
            // 验证医生是否存在
            Doctor existing = doctorMapper.selectById(doctorId);
            if (existing == null) {
                return Result.failed("医生信息不存在");
            }
            
            // 设置ID和更新时间，不允许修改的字段保持不变
            doctor.setId(doctorId);
            doctor.setUpdateTime(LocalDateTime.now());
            
            // 只更新允许修改的字段
            int rows = doctorMapper.update(doctor);
            if (rows > 0) {
                return Result.success("更新成功");
            }
            return Result.failed("更新失败");
        } catch (Exception e) {
            return Result.failed("更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public Result<String> uploadDoctorAvatar(@RequestHeader("Authorization") String auth,
                                         @RequestParam("file") MultipartFile file) {
    try {
        Long doctorId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ", ""));
        if (doctorId == null) return Result.failed("未登录");

        // 校验文件
        if (file.isEmpty()) return Result.failed("请选择图片");
        if (!file.getContentType().startsWith("image/"))
            return Result.failed("仅支持图片格式");
        if (file.getSize() > 5 * 1024 * 1024)
            return Result.failed("图片不能超过5MB");

        // 保存文件
        Path dir = Paths.get(System.getProperty("user.dir"), "uploads", "avatar");
        Files.createDirectories(dir);
        String ext = Optional.ofNullable(file.getOriginalFilename())
                             .filter(n -> n.contains("."))
                             .map(n -> n.substring(n.lastIndexOf(".")))
                             .orElse(".jpg");
        String name = UUID.randomUUID() + ext;
        Path target = dir.resolve(name);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String url = "/uploads/avatar/" + name;
        doctorMapper.updateAvatarUrl(doctorId, url, LocalDateTime.now());
        return Result.success("上传成功", url);
    } catch (Exception e) {
        return Result.failed("上传失败: " + e.getMessage());
    }
}
}
