package com.springai.openai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    ChatOptions chatOptions = ChatOptions.builder().model("gpt-4o-mini")
            .temperature(0.8).build();

    @Bean(name = "chatMemoryClientConfig")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .build();
    }
}
