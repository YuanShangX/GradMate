package com.gradmate.gradmate_springboot.config;

import com.volcengine.ark.runtime.service.ArkService;
import dev.langchain4j.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI配置类
 * 配置火山引擎豆包模型
 */
@Configuration
public class AiConfig {
    
    @Value("${ai.volcengine.api-key}")
    private String apiKey;
    
    @Value("${ai.volcengine.base-url}")
    private String baseUrl;
    
    @Value("${ai.volcengine.model-name}")
    private String modelName;
    
    @Value("${ai.volcengine.temperature}")
    private Double temperature;
    
    @Value("${ai.volcengine.max-tokens}")
    private Integer maxTokens;
    
    @Value("${ai.chat.memory-size}")
    private Integer memorySize;
    
    /**
     * 配置火山引擎服务
     */
    @Bean
    public ArkService arkService() {
        return ArkService.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }
    
    /**
     * 配置聊天记忆存储
     * 每个用户维护独立的会话上下文
     */
    @Bean
    public Map<Long, ChatMemory> chatMemoryStore() {
        return new ConcurrentHashMap<>();
    }
    
    /**
     * 获取记忆窗口大小
     */
    @Bean
    public Integer memorySize() {
        return memorySize;
    }
    
    /**
     * 获取模型名称
     */
    @Bean
    public String modelName() {
        return modelName;
    }
    
    /**
     * 获取温度参数
     */
    @Bean
    public Double temperature() {
        return temperature;
    }
    
    /**
     * 获取最大token数
     */
    @Bean
    public Integer maxTokens() {
        return maxTokens;
    }
}
