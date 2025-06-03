package org.isoft.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.defaultSystem("你是航班管理系统的一名ai助手"+"帮助乘客解决在乘坐飞机时遇到的问题").build();
    }
}
