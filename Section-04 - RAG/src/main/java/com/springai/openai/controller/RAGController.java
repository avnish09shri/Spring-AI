package com.springai.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/rag")
public class RAGController {

    private final ChatClient chatClient;
    private final ChatClient webSearchRAGChatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    private Resource promptTemplate;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource hrTemplate;

    public RAGController(@Qualifier("chatMemoryClientConfig") ChatClient chatClient,
                         @Qualifier("webSearchRAGChatClient") ChatClient webSearchRAGChatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.webSearchRAGChatClient = webSearchRAGChatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/random/chat")
    public ResponseEntity<String> randomChat(@RequestHeader("username") String username,
                                             @RequestParam("message") String message) {

        /*SearchRequest searchRequest = SearchRequest.builder()
                .query(message).topK(3).similarityThreshold(0.5)
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        String context = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));*/


        String result = chatClient.prompt()/*.system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("documents", context))*/
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message).call().content();

        return ResponseEntity.ok(result);

    }

    @GetMapping("/document/chat")
    public ResponseEntity<String> documentChat(@RequestHeader("username") String username,
                                             @RequestParam("message") String message) {

        /*SearchRequest searchRequest = SearchRequest.builder()
                .query(message).topK(3).similarityThreshold(0.5)
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        String context = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));*/

        String result = chatClient.prompt()/*.system(promptSystemSpec -> promptSystemSpec.text(hrTemplate)
                        .param("documents", context))*/
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message).call().content();

        return ResponseEntity.ok(result);

    }

    @GetMapping("/web-search/chat")
    public ResponseEntity<String> webSearchChat(@RequestHeader("username")
                                                String username, @RequestParam("message") String message) {
        String answer = webSearchRAGChatClient.prompt()
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }
}
