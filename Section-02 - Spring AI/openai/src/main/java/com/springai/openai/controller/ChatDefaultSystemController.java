package com.springai.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class ChatDefaultSystemController {

    private final ChatClient chatClient;

    public ChatDefaultSystemController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String getResponse(@RequestParam("message") String message) {
        return chatClient.prompt()
                .system("""
                        I am an internal service-desk assistant. I am here to guide you the resetting
                        password, open any IT ticket etc.
                        """) //overriding the above default system here.
                .user(message)
                .call().content();
    }
}
