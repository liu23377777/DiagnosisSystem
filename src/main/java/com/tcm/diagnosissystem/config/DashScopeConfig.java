package com.tcm.diagnosissystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.dashscope")
public class DashScopeConfig {
    private String apiKey;
    private String model;
    private String baseUrl;
    /**
     * 获取请求头
     */
    public String getAuthorizationHeader() {
        return "Bearer " + apiKey;
    }
    public String getBaseeUrl() {
        return baseUrl;
    }
    public void setBaseeUrl(String baseeUrl) {
        this.baseUrl = baseeUrl;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
