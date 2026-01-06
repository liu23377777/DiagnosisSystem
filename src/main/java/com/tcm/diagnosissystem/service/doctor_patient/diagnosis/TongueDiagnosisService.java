package com.tcm.diagnosissystem.service.doctor_patient.diagnosis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.tcm.diagnosissystem.dto.response.doctor.DiagnosisComparisonResponse;
import com.tcm.diagnosissystem.dto.response.doctor_patient.DiagnosisStatisticsResponse;
import com.tcm.diagnosissystem.dto.response.doctor.HealthTrendResponse;
import com.tcm.diagnosissystem.entity.patient.TongueDiagnosis;
import com.tcm.diagnosissystem.mapper.doctor.TongueDiagnosisMapper;
import com.tcm.diagnosissystem.service.patient.ai.QwenVisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 舌诊服务
 * 负责舌诊图片的分析、存储和历史记录管理
 */
@Service
public class TongueDiagnosisService {

    private static final Logger log = LoggerFactory.getLogger(TongueDiagnosisService.class);

    @Autowired
    private QwenVisionService qwenVisionService;

    @Autowired
    private TongueDiagnosisMapper diagnosisMapper;

    // 图片存储路径（生产环境建议使用OSS）
    private static final String UPLOAD_DIR = "uploads/tongue/";

    // 图片大小限制：10MB
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    /**
     * 分析舌诊图片
     *
     * @param userId 用户ID
     * @param image 舌头图片
     * @return 舌诊分析结果
     */
    @Transactional
    public TongueDiagnosis analyzeTongue(Long userId, MultipartFile image) {
        try {
            log.info("========== 开始舌诊分析 ==========");
            log.info("用户ID: {}", userId);
            log.info("图片名称: {}", image.getOriginalFilename());
            log.info("图片大小: {} bytes", image.getSize());

            // 1. 验证图片
            validateImage(image);

            // 2. 保存图片
            String imageUrl = saveImage(image);
            log.info("图片保存成功: {}", imageUrl);

            // 3. 构建专业Prompt
            String prompt = qwenVisionService.buildTongueDiagnosisPrompt();

            // 4. 调用AI分析
            String aiResponse = qwenVisionService.analyzeTongueImage(image, prompt);
            log.info("AI原始返回长度: {} 字符", aiResponse.length());
            log.info("AI原始返回: {}", aiResponse);

            // 5. 清洗JSON（移除markdown标记）
            String cleanedJson = cleanJsonResponse(aiResponse);
            log.info("清洗后JSON: {}", cleanedJson);

            // 6. 解析JSON
            JSONObject jsonObject = parseJsonResponse(cleanedJson);

            // 7. 创建诊断记录
            TongueDiagnosis diagnosis = new TongueDiagnosis();
            diagnosis.setUserId(userId);
            diagnosis.setImageUrl(imageUrl);
            diagnosis.setTongueColor(jsonObject.getString("tongueColor"));
            diagnosis.setTongueShape(jsonObject.getString("tongueShape"));
            diagnosis.setCoatingColor(jsonObject.getString("coatingColor"));
            diagnosis.setCoatingQuality(jsonObject.getString("coatingTexture"));
            diagnosis.setSyndrome(jsonObject.getString("syndrome"));
            diagnosis.setPathogenesis(jsonObject.getString("analysis"));
            diagnosis.setAdvice(jsonObject.getString("suggestions"));
            diagnosis.setFullAnalysis(cleanedJson);  // 保存清洗后的JSON
            diagnosis.setCreateTime(LocalDateTime.now());

            // 8. 保存到数据库
            diagnosisMapper.insert(diagnosis);

            log.info("舌诊分析完成，记录ID: {}", diagnosis.getId());
            log.info("证型: {}", diagnosis.getSyndrome());
            log.info("========================================");

            return diagnosis;

        } catch (Exception e) {
            log.error("舌诊分析失败", e);
            throw new RuntimeException("舌诊分析失败: " + e.getMessage());
        }
    }

    /**
     * 验证图片格式和大小
     */
    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("图片不能为空");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持图片格式（jpg、png、jpeg等）");
        }

        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("图片大小不能超过10MB");
        }

        // 验证文件扩展名
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.toLowerCase().endsWith(".jpg") &&
                        !originalFilename.toLowerCase().endsWith(".jpeg") &&
                        !originalFilename.toLowerCase().endsWith(".png"))) {
            throw new RuntimeException("只支持jpg、jpeg、png格式的图片");
        }
    }

    /**
     * 保存图片到本地
     */
    private String saveImage(MultipartFile image) throws IOException {
        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new IOException("无法创建上传目录");
            }
        }

        // 生成唯一文件名
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.write(filePath, image.getBytes());

        return "/uploads/tongue/" + filename; // 返回访问路径
    }

    /**
     * 清洗AI返回的JSON
     * 移除markdown标记和多余字符
     */
    private String cleanJsonResponse(String response) {
        if (response == null || response.isEmpty()) {
            log.warn("AI返回为空，使用默认JSON");
            return "{}";
        }

        // 移除各种markdown代码块标记
        String cleaned = response
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .replaceAll("'''json\\s*", "")
                .replaceAll("'''\\s*", "")
                .replaceAll("`", "")
                .trim();

        // 提取第一个完整的JSON对象
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        } else {
            log.warn("无法找到完整的JSON对象");
            return "{}";
        }

        return cleaned;
    }

    /**
     * 解析JSON响应，增强容错
     */
    private JSONObject parseJsonResponse(String jsonString) {
        try {
            return JSON.parseObject(jsonString);
        } catch (Exception e) {
            log.error("JSON解析失败", e);

            // 返回默认值
            JSONObject defaultJson = new JSONObject();
            defaultJson.put("tongueColor", "未识别");
            defaultJson.put("tongueShape", "未识别");
            defaultJson.put("coatingColor", "未识别");
            defaultJson.put("coatingTexture", "未识别");
            defaultJson.put("specialFeatures", "无");
            defaultJson.put("syndrome", "未识别");
            defaultJson.put("analysis", "图片分析失败，请重新上传清晰的舌头照片");
            defaultJson.put("suggestions", "请在良好光线下重新拍摄舌头照片，确保舌头完整清晰");

            return defaultJson;
        }
    }

    /**
     * 获取用户舌诊历史
     *
     * @param userId 用户ID
     * @return 舌诊历史列表
     */
    public List<TongueDiagnosis> getUserHistory(Long userId) {
        log.info("查询用户{}的舌诊历史", userId);
        return diagnosisMapper.findByUserId(userId);
    }

    /**
     * 获取舌诊详情
     *
     * @param id 舌诊记录ID
     * @return 舌诊详情
     */
    public TongueDiagnosis getDetail(Long id) {
        log.info("查询舌诊记录详情，ID: {}", id);
        TongueDiagnosis diagnosis = diagnosisMapper.findById(id);

        if (diagnosis == null) {
            throw new RuntimeException("舌诊记录不存在");
        }

        return diagnosis;
    }

    /**
     * 删除舌诊记录
     *
     * @param id 舌诊记录ID
     * @param userId 用户ID（用于权限验证）
     */
    @Transactional
    public void deleteDiagnosis(Long id, Long userId) {
        log.info("删除舌诊记录，ID: {}, 用户ID: {}", id, userId);

        TongueDiagnosis diagnosis = diagnosisMapper.findById(id);

        if (diagnosis == null) {
            throw new RuntimeException("舌诊记录不存在");
        }

        if (!diagnosis.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除他人的舌诊记录");
        }

        // 删除图片文件
        try {
            String imageUrl = diagnosis.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String filePath = imageUrl.replace("/uploads/tongue/", UPLOAD_DIR);
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            log.error("删除图片文件失败", e);
        }

        // 删除数据库记录
        diagnosisMapper.deleteById(id);
        log.info("舌诊记录删除成功");
    }

    /**
     * 获取用户最近一次舌诊记录
     *
     * @param userId 用户ID
     * @return 最近的舌诊记录
     */
    public TongueDiagnosis getLatestDiagnosis(Long userId) {
        log.info("查询用户{}最近的舌诊记录", userId);
        List<TongueDiagnosis> history = diagnosisMapper.findByUserId(userId);

        if (history == null || history.isEmpty()) {
            return null;
        }

        // 返回最新的一条记录
        return history.get(0);
    }




    /**
     * 分页查询舌诊历史
     */
    public List<TongueDiagnosis> getUserHistoryPage(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return diagnosisMapper.findByUserIdWithPage(userId, offset, pageSize);
    }

    /**
     * 统计用户舌诊记录总数
     */
    public long countUserDiagnosis(Long userId) {
        return diagnosisMapper.countByUserId(userId);
    }

    /**
     * 根据证型查询舌诊记录
     */
    public List<TongueDiagnosis> findBySyndrome(String syndrome) {
        return diagnosisMapper.findBySyndrome(syndrome);
    }

    /**
     * 根据日期范围查询
     */
    public List<TongueDiagnosis> findByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return diagnosisMapper.findByDateRange(userId, startDate, endDate);
    }

    /**
     * 健康趋势分析
     */
    public HealthTrendResponse analyzeHealthTrend(Long userId, int days) {
        log.info("开始分析用户{}最近{}天的健康趋势", userId, days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();

        List<TongueDiagnosis> records = diagnosisMapper.findByDateRange(userId, startDate, endDate);

        if (records.isEmpty()) {
            throw new RuntimeException("该时间段内无舌诊记录");
        }

        // 按时间排序（最新的在前）
        records.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

        HealthTrendResponse response = new HealthTrendResponse();
        response.setRecordCount(records.size());
        response.setAnalyzeDays(days);
        response.setLatestDiagnosis(records.get(0));
        response.setEarliestDiagnosis(records.get(records.size() - 1));

        // 统计证型分布
        Map<String, Long> syndromeCount = records.stream()
                .collect(Collectors.groupingBy(
                        TongueDiagnosis::getSyndrome,
                        Collectors.counting()
                ));
        response.setSyndromeDistribution(syndromeCount);

        // 舌色变化趋势（按时间正序）
        List<String> colorTrend = records.stream()
                .sorted(Comparator.comparing(TongueDiagnosis::getCreateTime))
                .map(TongueDiagnosis::getTongueColor)
                .collect(Collectors.toList());
        response.setColorTrend(colorTrend);

        // 舌苔变化趋势
        List<String> coatingTrend = records.stream()
                .sorted(Comparator.comparing(TongueDiagnosis::getCreateTime))
                .map(TongueDiagnosis::getCoatingColor)
                .collect(Collectors.toList());
        response.setCoatingTrend(coatingTrend);

        // 调用AI生成趋势分析
        String prompt = buildTrendAnalysisPrompt(records, days);
        String aiAnalysis = callAIForTrendAnalysis(prompt);
        response.setTrendAnalysis(aiAnalysis);

        // 判断健康状态
        String healthStatus = determineHealthStatus(records);
        response.setHealthStatus(healthStatus);

        // 生成改善建议
        List<String> suggestions = generateSuggestions(records, healthStatus);
        response.setSuggestions(suggestions);

        log.info("趋势分析完成，健康状态: {}", healthStatus);
        return response;
    }

    /**
     * 对比两次诊断
     */
    public DiagnosisComparisonResponse compareDiagnosis(Long oldId, Long newId, Long userId) {
        log.info("开始对比诊断记录 {} 和 {}", oldId, newId);

        TongueDiagnosis oldDiagnosis = diagnosisMapper.findById(oldId);
        TongueDiagnosis newDiagnosis = diagnosisMapper.findById(newId);

        if (oldDiagnosis == null || newDiagnosis == null) {
            throw new RuntimeException("诊断记录不存在");
        }

        // 权限验证
        if (!oldDiagnosis.getUserId().equals(userId) || !newDiagnosis.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问他人的诊断记录");
        }

        DiagnosisComparisonResponse response = new DiagnosisComparisonResponse();
        response.setOldDiagnosis(oldDiagnosis);
        response.setNewDiagnosis(newDiagnosis);

        // 计算时间差
        long daysBetween = ChronoUnit.DAYS.between(
                oldDiagnosis.getCreateTime(),
                newDiagnosis.getCreateTime()
        );
        response.setDaysBetween((int) daysBetween);

        // 提取差异点
        List<String> differences = findDifferences(oldDiagnosis, newDiagnosis);
        response.setDifferences(differences);

        // 调用AI分析变化
        String prompt = buildComparisonPrompt(oldDiagnosis, newDiagnosis, daysBetween);
        String changeAnalysis = callAIForComparison(prompt);
        response.setChangeAnalysis(changeAnalysis);

        // 判断进展状态
        String progressStatus = determineProgressStatus(oldDiagnosis, newDiagnosis);
        response.setProgressStatus(progressStatus);

        log.info("诊断对比完成，进展状态: {}", progressStatus);
        return response;
    }

    /**
     * 获取用户诊断统计
     */
    public DiagnosisStatisticsResponse getUserStatistics(Long userId) {
        log.info("开始统计用户{}的诊断数据", userId);

        DiagnosisStatisticsResponse response = new DiagnosisStatisticsResponse();

        // 总记录数
        response.setTotalCount(diagnosisMapper.countByUserId(userId));

        // 今日诊断数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now();
        List<TongueDiagnosis> todayRecords = diagnosisMapper.findByDateRange(userId, todayStart, todayEnd);
        response.setTodayCount((long) todayRecords.size());

        // 本周诊断数
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        List<TongueDiagnosis> weekRecords = diagnosisMapper.findByDateRange(userId, weekStart, todayEnd);
        response.setWeekCount((long) weekRecords.size());

        // 本月诊断数
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
        List<TongueDiagnosis> monthRecords = diagnosisMapper.findByDateRange(userId, monthStart, todayEnd);
        response.setMonthCount((long) monthRecords.size());

        // 获取所有记录用于统计
        List<TongueDiagnosis> allRecords = diagnosisMapper.findByUserId(userId);

        // 证型分布
        Map<String, Long> syndromeDistribution = allRecords.stream()
                .collect(Collectors.groupingBy(TongueDiagnosis::getSyndrome, Collectors.counting()));
        response.setSyndromeDistribution(syndromeDistribution);

        // 舌色分布
        Map<String, Long> colorDistribution = allRecords.stream()
                .collect(Collectors.groupingBy(TongueDiagnosis::getTongueColor, Collectors.counting()));
        response.setColorDistribution(colorDistribution);

        // 最常见证型
        String mostCommon = syndromeDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无");
        response.setMostCommonSyndrome(mostCommon);

        // 每周平均诊断次数
        if (!allRecords.isEmpty()) {
            TongueDiagnosis earliest = allRecords.stream()
                    .min(Comparator.comparing(TongueDiagnosis::getCreateTime))
                    .orElse(null);

            if (earliest != null) {
                long totalWeeks = ChronoUnit.WEEKS.between(earliest.getCreateTime(), LocalDateTime.now()) + 1;
                double avgPerWeek = (double) allRecords.size() / totalWeeks;
                response.setAverageDiagnosisPerWeek(Math.round(avgPerWeek * 100.0) / 100.0);
            }
        }

        log.info("统计完成，总记录数: {}", response.getTotalCount());
        return response;
    }

// ========== 辅助方法 ==========

    private String buildTrendAnalysisPrompt(List<TongueDiagnosis> records, int days) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("根据患者最近%d天的%d次舌诊记录，分析健康趋势：\n\n", days, records.size()));

        // 按时间正序
        records.stream()
                .sorted(Comparator.comparing(TongueDiagnosis::getCreateTime))
                .forEach(r -> {
                    sb.append(String.format("【%s】舌质:%s | 舌苔:%s | 证型:%s\n",
                            r.getCreateTime().toLocalDate(),
                            r.getTongueColor(),
                            r.getCoatingColor(),
                            r.getSyndrome()
                    ));
                });

        sb.append("\n请分析：\n");
        sb.append("1. 健康状况是改善、稳定还是恶化\n");
        sb.append("2. 主要变化趋势\n");
        sb.append("3. 需要注意的问题\n");
        sb.append("4. 下一步调理建议\n");

        return sb.toString();
    }

    private String callAIForTrendAnalysis(String prompt) {
        try {
            // 这里调用AI接口（可以复用 QwenVisionService 或单独调用文本API）
            // 简化处理：直接返回文本分析
            return "AI正在分析中...（请根据实际情况接入AI文本接口）";
        } catch (Exception e) {
            log.error("AI趋势分析失败", e);
            return "AI分析暂时不可用";
        }
    }

    private String determineHealthStatus(List<TongueDiagnosis> records) {
        if (records.size() < 2) {
            return "stable";
        }

        // 简单逻辑：对比最新和最早的证型
        String latestSyndrome = records.get(0).getSyndrome();
        String earliestSyndrome = records.get(records.size() - 1).getSyndrome();

        // 可以根据中医理论判断证型的严重程度
        // 这里简化处理
        if (latestSyndrome.contains("虚") && !earliestSyndrome.contains("虚")) {
            return "declining";
        } else if (!latestSyndrome.contains("虚") && earliestSyndrome.contains("虚")) {
            return "improving";
        }

        return "stable";
    }

    private List<String> generateSuggestions(List<TongueDiagnosis> records, String healthStatus) {
        List<String> suggestions = new ArrayList<>();

        if ("declining".equals(healthStatus)) {
            suggestions.add("建议及时就医，进行专业诊断");
            suggestions.add("注意休息，避免过度劳累");
            suggestions.add("调整饮食，清淡为主");
        } else if ("improving".equals(healthStatus)) {
            suggestions.add("继续保持良好的生活习惯");
            suggestions.add("坚持规律作息");
            suggestions.add("适度运动，增强体质");
        } else {
            suggestions.add("保持现状，定期复查");
            suggestions.add("均衡饮食，避免偏食");
        }

        return suggestions;
    }

    private List<String> findDifferences(TongueDiagnosis old, TongueDiagnosis newD) {
        List<String> differences = new ArrayList<>();

        if (!old.getTongueColor().equals(newD.getTongueColor())) {
            differences.add(String.format("舌色: %s → %s", old.getTongueColor(), newD.getTongueColor()));
        }

        if (!old.getTongueShape().equals(newD.getTongueShape())) {
            differences.add(String.format("舌形: %s → %s", old.getTongueShape(), newD.getTongueShape()));
        }

        if (!old.getCoatingColor().equals(newD.getCoatingColor())) {
            differences.add(String.format("舌苔: %s → %s", old.getCoatingColor(), newD.getCoatingColor()));
        }

        if (!old.getSyndrome().equals(newD.getSyndrome())) {
            differences.add(String.format("证型: %s → %s", old.getSyndrome(), newD.getSyndrome()));
        }

        if (differences.isEmpty()) {
            differences.add("舌象无明显变化");
        }

        return differences;
    }

    private String buildComparisonPrompt(TongueDiagnosis old, TongueDiagnosis newD, long days) {
        return String.format("""
        对比分析两次舌诊结果（相隔%d天）：
        
        【旧诊断】
        舌质: %s | 舌苔: %s | 证型: %s
        
        【新诊断】
        舌质: %s | 舌苔: %s | 证型: %s
        
        请分析变化趋势和原因，给出调理建议。
        """,
                days,
                old.getTongueColor(), old.getCoatingColor(), old.getSyndrome(),
                newD.getTongueColor(), newD.getCoatingColor(), newD.getSyndrome()
        );
    }

    private String callAIForComparison(String prompt) {
        // 同上，调用AI文本接口
        return "AI对比分析中...";
    }

    private String determineProgressStatus(TongueDiagnosis old, TongueDiagnosis newD) {
        // 简化逻辑：根据证型判断
        if (newD.getSyndrome().contains("虚") && !old.getSyndrome().contains("虚")) {
            return "worsened";
        } else if (!newD.getSyndrome().contains("虚") && old.getSyndrome().contains("虚")) {
            return "improved";
        }
        return "stable";
    }

    // 获取待审核诊断记录
    public List<TongueDiagnosis> getPendingDiagnosisForDoctor(Long doctorId) {
        return diagnosisMapper.selectPendingList();  // ✅ 使用 diagnosisMapper
    }

    // 医生确认诊断
    public void confirmDiagnosis(Long diagnosisId, Long doctorId, String finalDiagnosis, String finalAdvice) {
        TongueDiagnosis diagnosis = diagnosisMapper.selectById(diagnosisId);  // ✅ 使用 diagnosisMapper
        if (diagnosis == null) {
            throw new RuntimeException("诊断记录不存在");
        }

        diagnosis.setDoctorId(doctorId);
        diagnosis.setFinalSyndrome(finalDiagnosis);
        diagnosis.setFinalAdvice(finalAdvice);
        diagnosis.setStatus(1);
        diagnosis.setReviewTime(LocalDateTime.now());

        diagnosisMapper.updateById(diagnosis);  // ✅ 使用 diagnosisMapper
    }

    /**
     * 根据ID获取诊断记录
     */
    public TongueDiagnosis getDiagnosisById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("诊断记录ID不能为空");
        }

        TongueDiagnosis diagnosis = diagnosisMapper.selectById(id);
        if (diagnosis == null) {
            throw new RuntimeException("诊断记录不存在，ID: " + id);
        }

        return diagnosis;
    }



}
