package com.tcm.diagnosissystem.service.patient.ai;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.tcm.diagnosissystem.config.DashScopeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct; 

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class QwenVisionService {

    private static final Logger log = LoggerFactory.getLogger(QwenVisionService.class);

    @Autowired
    private DashScopeConfig config;

    private String knowledgeBase;  // 知识库内容

    /**
     * 启动时加载知识库
     */
    @PostConstruct
    public void loadKnowledge() {
        try {
            Resource resource = new ClassPathResource("tongue-diagnosis-knowledge.txt");

            if (!resource.exists()) {
                knowledgeBase = "";
                return;
            }

            knowledgeBase = new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            log.error("知识库加载失败", e);
            knowledgeBase = "";
        }
    }

    public String analyzeTongueImage(MultipartFile image, String prompt) {
        try {

            // 将图片转换为Base64
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String imageUrl = "data:image/jpeg;base64," + base64Image;

            log.info("图片已转换为Base64，长度: {}", base64Image.length());

            // 构建消息
            List<MultiModalMessage> messages = Arrays.asList(
                    MultiModalMessage.builder()
                            .role(Role.SYSTEM.getValue())
                            .content(Arrays.asList(
                                    Collections.singletonMap("text", "你是一位资深中医舌诊专家，擅长舌诊分析，然后如果用户上传的图片是舌诊图片，请根据舌诊图片进行分析，然后如果用户上传的图片不是舌诊图，就委婉提出错误，并要求用户重新上传舌诊图片。")
                            ))
                            .build(),
                    MultiModalMessage.builder()
                            .role(Role.USER.getValue())
                            .content(Arrays.asList(
                                    Collections.singletonMap("image", imageUrl),
                                    Collections.singletonMap("text", prompt)
                            ))
                            .build()
            );

            // 构建参数
            MultiModalConversationParam param = MultiModalConversationParam.builder()
        .apiKey(config.getApiKey())
        .model("qwen-vl-plus")
        .messages(messages)
        .topP(0.8)
        .temperature(0.3f)
        .build();

            // Max tokens 暂不支持: MultiModalConversationParam 没有 setMaxTokens 方法

            // 调用API
            MultiModalConversation conversation = new MultiModalConversation();
            MultiModalConversationResult result = conversation.call(param);

            // 提取结果
            Map<String, Object> firstContent = (Map<String, Object>) result.getOutput()
                    .getChoices().get(0)
                    .getMessage()
                    .getContent().get(0);

            String response = (String) firstContent.get("text");

            log.info("舌诊分析成功，返回字符数: {}", response.length());
            return response;

        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            log.error("阿里云API调用失败", e);
            throw new RuntimeException("AI服务调用失败: " + e.getMessage());
        } catch (IOException e) {
            log.error("图片处理失败", e);
            throw new RuntimeException("图片处理失败: " + e.getMessage());
        }
    }


    /**
     * 构建包含知识库的Prompt
     */
    public String buildTongueDiagnosisPrompt() {
        String knowledgeSection = "";
        if (knowledgeBase != null && !knowledgeBase.isEmpty()) {
            knowledgeSection = """
                
                # 专业知识库参考
                
                """ + knowledgeBase + """
                
                """;
        }

        return """
            你是一位资深中医舌诊专家。
            
            """ + knowledgeSection + """
            
            # ⚠️ 关键诊断要求（必须严格遵守）
            
            请你**非常仔细**地观察这张舌象照片，不要给出模板化答案！
            
            ## 第1步：逐项检查（必须完成）
            
            请依次检查以下特征：
            
            1️⃣ **舌体大小**否**充满口腔**？（胖大舌）
               - [ ] 舌体是否**瘦小**？（瘦薄舌）
               - [ ] 还是正常大小？
            
            2️⃣ **舌边齿痕**（重要！）
               - [ ] **舌边是否有明显的牙齿压迫痕迹？**（齿痕舌）
               - [ ] 如果有齿痕，这是**脾虚湿盛**的典型特征！
            
            3️⃣ **舌苔厚度**
               - [ ] 能否**透过舌苔看见舌质**？
               - [ ] 如果看不见，说明是**厚苔**（病邪深重）
               - [ ] 如果能看见，才是薄苔
            
            4️⃣ **舌苔质地**
               - [ ] 舌苔是否**黏腻、油腻**？（腻苔 = 湿浊）
               - [ ] 舌苔是否**干燥、粗糙**？（燥苔 = 津液伤）
               - [ ] 还是适度湿润？
            
            5️⃣ **舌色判断**
               - [ ] 舌色是否**明显浅淡**？（淡白 = 气血虚）
               - [ ] 舌色是否**鲜红或深红**？（红绛 = 热证）
               - [ ] 还是淡红适中？
            
            ## 第2步：综合判断
            
            根据上述检查结果，对照以下证型：
            
            **如果观察到：胖大 + 齿痕 + 白厚腻苔**
            → **必须诊断为"脾虚湿盛"**，不能说是正常！
            
            **如果观察到：瘦薄 + 红绛 + 少苔/剥苔**
            → **必须诊断为"阴虚火旺"**
            
            **如果观察到：红舌 + 黄厚腻苔**
            → **必须诊断为"湿热内蕴"**
            
            ## 第3步：输出结果
            
            严格按照以下JSON格式返回（不要添加markdown标记）：
            
            {
              "tongueColor": "舌质颜色（中文：淡白/淡红/鲜红/红绛/紫暗）",
              "tongueShape": "舌体形态（中文：正常/胖大有齿痕/瘦薄/裂纹）",
              "coatingColor": "苔色（中文：薄白/白厚腻/薄黄/黄厚腻）",
              "coatingTexture": "苔质（中文：润/燥/滑/腻）",
              "specialFeatures": "特殊特征（如：齿痕明显、舌尖红、裂纹等，无则填"无"）",
              "syndrome": "证型（必须从知识库中匹配，不要说"正常舌象"除非真的完全正常）",
              "analysis": "病机分析（150-200字，解释为什么会出现这种舌象，必须引用中医理论）",
              "suggestions": "调理建议（200-300字，包含：1.饮食建议 2.中药方剂 3.生活方式）"
            }
            
            ## ⚠️ 最重要的提醒
            
            - **不要轻易给出"正常舌象"的诊断！**
            - **如果看到齿痕，必须诊断为脾虚或湿盛相关证型**
            - **如果舌苔厚腻，必须诊断为湿浊相关证型**
            - **必须基于图片实际观察，不要使用通用模板**
            - **如果图片模糊看不清，请在analysis中明确说明"图片不清晰，建议重新拍摄"**
            """;
    }


}
