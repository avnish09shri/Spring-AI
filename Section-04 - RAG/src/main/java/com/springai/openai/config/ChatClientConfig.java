package com.springai.openai.config;

import com.springai.openai.advisor.TokenUsageAuditAdvisor;
import com.springai.openai.rag.PIIMaskingDocumentPostProcessor;
import org.springframework.ai.chat.cache.semantic.SemanticCacheAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.VectorStoreRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    ChatOptions.Builder chatOptions = ChatOptions.builder().model("gpt-4o-mini")
            .temperature(0.8);

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder().maxMessages(10).
                chatMemoryRepository(jdbcChatMemoryRepository).build();

    }

    @Bean(name = "chatMemoryClientConfig")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
                                 RetrievalAugmentationAdvisor retrievalAugmentationAdvisor, SemanticCacheAdvisor semanticCacheAdvisor) {

        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return chatClientBuilder
                .defaultOptions(chatOptions)
                .defaultAdvisors(List.of(loggerAdvisor, messageChatMemoryAdvisor, tokenUsageAdvisor, retrievalAugmentationAdvisor, semanticCacheAdvisor))
                .build();
    }

    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(TranslationQueryTransformer.builder()  // impl of pre-retrieval
                        .chatClientBuilder(chatClientBuilder.clone()).targetLanguage("english")
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore)
                        .topK(3).similarityThreshold(0.5)
                        .build())
                .documentPostProcessors(PIIMaskingDocumentPostProcessor.builder())
                .build();
    }
}
