package com.springai.openai.controller;

import com.springai.openai.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StructuredOutputController {

    private final ChatClient client;

    public StructuredOutputController(ChatClient.Builder chatClientBuilder) {
        this.client = chatClientBuilder
                .defaultOptions(ChatOptions.builder()
                        .model("gpt-4o-mini").build())
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @GetMapping("/chat-bean")
    public ResponseEntity<CountryCities> chatBean(@RequestParam String message) {
        CountryCities countryCities = client
                .prompt()
                .user(message)
                .call()
                .entity(CountryCities.class);
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("chat-list")
    public ResponseEntity<List<String>> chatList(@RequestParam String message) {
        List<String> response = client
                .prompt()
                .user(message)
                .call()
                .entity(new ListOutputConverter());

        return ResponseEntity.ok(response);
    }

    @GetMapping("chat-map")
    public ResponseEntity<Map<String, Object>> chatMap(@RequestParam String message) {
        Map<String, Object> response = client
                .prompt()
                .user(message)
                .call()
                .entity(new MapOutputConverter());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat-bean2")
    public ResponseEntity<CountryCities> chatBeanAlt(@RequestParam String message) {
        CountryCities countryCities = client.prompt()
                .user(message)
                .call()
                .entity(new BeanOutputConverter<>(CountryCities.class));
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("chat-bean-list")
    public ResponseEntity<List<CountryCities>> chatBeanList(@RequestParam String message) {
        List<CountryCities> response = client
                .prompt()
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<CountryCities>>() {
                });

        return ResponseEntity.ok(response);
    }

}
