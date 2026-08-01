package com.springai.openai.rag;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HRPolicyDataLoader {

    private final VectorStore vectorStore;

    @Value("classpath:Eazybytes_HR_Policies.pdf")
    Resource policyFile;

    @PostConstruct
    public void loadPdf() {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(policyFile);
        List<Document> documents = tikaDocumentReader.get();

        TextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .withMaxNumChunks(400)
                .build();

        vectorStore.add(textSplitter.split(documents));
    }

}
