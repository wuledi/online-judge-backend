package com.wuledi.ai.config;

import com.wuledi.ai.documentreader.MyMarkdownDocumentReader;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * PgVector 向量数据库配置
 * <a href="https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html#_manual_configuration">...</a>
 *
 * @author wuledi
 */
// @Configuration  // todo 使用时，取消注释, 质谱ai异常,  待解决:维度?
public class PgVectorVectorStoreConfig {

    @Resource
    private MyMarkdownDocumentReader myMarkdownDocumentReader; // 加载文档

    /**
     * 初始化 PgVector 向量数据库
     *
     * @param jdbcTemplate          JdbcTemplate
     * @param zhiPuAiEmbeddingModel EmbeddingModel
     * @return VectorStore
     */
    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel zhiPuAiEmbeddingModel) {
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, zhiPuAiEmbeddingModel)
                .dimensions(1536)                    // 可选：默认为模型尺寸或1536
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)       // 可选：默认为 COSINE_DISTANCE
                .indexType(PgVectorStore.PgIndexType.HNSW)                     // 可选: 默认为 HNSW
                .initializeSchema(true)              // 可选: 默认为 false
                .schemaName("public")                // 可选: 默认为 "public"
                .vectorTableName("vector_store")     // 可选: 默认为 "vector_store"
                .maxDocumentBatchSize(10000)         // 可选: 默认为 10000
                .build();
        // 加载文档
        List<Document> documents = myMarkdownDocumentReader.loadMarkdowns();
        vectorStore.add(documents);
        return vectorStore;
    }
}
