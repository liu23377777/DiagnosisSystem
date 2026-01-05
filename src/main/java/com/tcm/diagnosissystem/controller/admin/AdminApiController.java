package com.tcm.diagnosissystem.controller.admin;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.Admin;
import com.tcm.diagnosissystem.entity.Doctor;
import com.tcm.diagnosissystem.entity.Patient;
import com.tcm.diagnosissystem.mapper.admin.AdminMapper;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import com.tcm.diagnosissystem.mapper.patient.PatientMapper;
import com.tcm.diagnosissystem.service.doctor.DoctorService;
import com.tcm.diagnosissystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  //返回前缀为 /api/admin
@RequestMapping("/api/admin")
public class AdminApiController {
    //依赖注入
    @Autowired private PatientMapper patientMapper;
    @Autowired private DoctorMapper doctorMapper;
    @Autowired private AdminMapper adminMapper;
    @Autowired private JwtUtil jwtUtil;//解析Jwt ，管理员段块的 JWT采用Header
    @Autowired private DoctorService doctorService;

    // ========== 患者管理 ==========
    @GetMapping("/patients")//查询所有患者
    public Result<List<Patient>> getAllPatients() {
        List<Patient> patients = patientMapper.selectAll();
        patients.forEach(p -> p.setPassword(null));//屏蔽密码字段
        return Result.success(patients);
    }

    @PutMapping("/patients/{id}/status")
    public Result<String> updatePatientStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");//获取状态参数
        if (status == null || (status != 0 && status != 1)) {
            return Result.failed("状态值无效");//核验参数
        }
        int rows = patientMapper.updateStatus(id, status, LocalDateTime.now());//调用更新方法
        return rows > 0 ? Result.success("更新成功") : Result.failed("更新失败");//1成功0失败
    }

    @PutMapping("/patients/{id}")//更新患者id
    public Result<String> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Patient existing = patientMapper.selectById(id);//获取患者id
        if (existing == null) {
            return Result.failed("患者不存在");
        }
        patient.setId(id);//设置id
        patient.setUpdateTime(LocalDateTime.now());//设置更新时间
        int rows = patientMapper.update(patient);
        return rows > 0 ? Result.success("更新成功") : Result.failed("更新失败");
    }

    // ========== 医生审核 ==========
    @GetMapping("/doctors/pending")
    public Result<?> pendingDoctors(){
        return Result.success(doctorService.findPending());
    }

    @PostMapping("/doctors/{id}/approve")
    public Result<?> approveDoctor(@PathVariable Long id){
        return doctorService.updateStatus(id,1)>0?Result.success("已通过审核"):Result.failed("操作失败");
    }

    @PostMapping("/doctors/{id}/reject")
    public Result<?> rejectDoctor(@PathVariable Long id){
        return doctorService.updateStatus(id,-1)>0?Result.success("已驳回"):Result.failed("操作失败");
    }

    // ========== 医生管理（已审核） ==========
    @GetMapping("/doctors")
    public Result<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorMapper.selectAll();
        doctors.forEach(d -> d.setPassword(null));
        return Result.success(doctors);
    }

    @PutMapping("/doctors/{id}/status")
    public Result<String> updateDoctorStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.failed("状态值无效");
        }
        int rows = doctorMapper.updateStatus(id, status, LocalDateTime.now());
        return rows > 0 ? Result.success("更新成功") : Result.failed("更新失败");
    }

    @PutMapping("/doctors/{id}")
    public Result<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        Doctor existing = doctorMapper.selectById(id);
        if (existing == null) {
            return Result.failed("医生不存在");
        }
        doctor.setId(id);
        doctor.setUpdateTime(LocalDateTime.now());
        int rows = doctorMapper.update(doctor);
        return rows > 0 ? Result.success("更新成功") : Result.failed("更新失败");
    }

    // ========== 统计 ==========
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        List<Patient> allPatients = patientMapper.selectAll();
        long totalPatients = allPatients.size();
        long activePatients = allPatients.stream().filter(p -> p.getStatus()!=null&&p.getStatus()==1).count();
        List<Doctor> allDoctors = doctorMapper.selectAll();
        long totalDoctors = allDoctors.size();
        long activeDoctors = allDoctors.stream().filter(d -> d.getStatus()!=null&&d.getStatus()==1).count();
        stats.put("totalPatients", totalPatients);
        stats.put("activePatients", activePatients);
        stats.put("disabledPatients", totalPatients-activePatients);
        stats.put("totalDoctors", totalDoctors);
        stats.put("activeDoctors", activeDoctors);
        stats.put("disabledDoctors", totalDoctors-activeDoctors);
        return Result.success(stats);
    }

    // ========== 管理员个人信息 ==========
    @GetMapping("/info")
    public Result<Admin> getAdminInfo(@RequestHeader("Authorization") String auth) {
        Long adminId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ", ""));
        Admin admin = adminMapper.selectById(adminId);
        if(admin!=null) admin.setPassword(null);
        return admin!=null?Result.success(admin):Result.failed("管理员不存在");
    }
    // 头像上传
    @PostMapping("/avatar")
    public Result<String> uploadAdminAvatar(@RequestHeader("Authorization") String auth,
                                            @RequestParam("file") MultipartFile file) {
        try {
            Long adminId = jwtUtil.getUserIdFromToken(auth.replace("Bearer ", ""));
            if (adminId == null) return Result.failed("未登录");

            if (file.isEmpty()) return Result.failed("请选择图片");
            if (!file.getContentType().startsWith("image/"))
                return Result.failed("仅支持图片格式");
            if (file.getSize() > 5 * 1024 * 1024)
                return Result.failed("图片不能超过5MB");

            Path dir = Paths.get(System.getProperty("user.dir"), "uploads", "avatar");
            Files.createDirectories(dir);
            String ext = Optional.ofNullable(file.getOriginalFilename())
                                 .filter(n -> n.contains("."))
                                 .map(n -> n.substring(n.lastIndexOf(".")))
                                 .orElse(".jpg");
            String name = UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/avatar/" + name;
            adminMapper.updateAvatarUrl(adminId, url, LocalDateTime.now());
            return Result.success("上传成功", url);
        } catch (Exception e) {
            return Result.failed("上传失败: " + e.getMessage());
        }
    }

    @PutMapping("/info")
    public Result<String> updateAdminInfo(@RequestHeader("Authorization") String auth,
                                          @RequestBody Admin admin){
        Long id = jwtUtil.getUserIdFromToken(auth.replace("Bearer ",""));
        admin.setId(id);
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.update(admin);
        return Result.success("更新成功");
    }
}
