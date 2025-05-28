package cn.edu.zust.se.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EmbeddingConfig {
    private final PgProperties pgProperties;

    public EmbeddingStore<TextSegment> EmbeddingStore(Long ragId){
        return PgVectorEmbeddingStore.builder()
                .table(pgProperties.getTable()+"_"+ragId)
                .createTable(true)
                .database(pgProperties.getDatabase())
                .port(pgProperties.getPort())
                .host(pgProperties.getHost())
                .user(pgProperties.getUser())
                .password(pgProperties.getPassword())
                .dimension(1024)
                .build();
    }
}
