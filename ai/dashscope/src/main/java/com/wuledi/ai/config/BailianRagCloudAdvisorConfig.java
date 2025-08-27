package com.wuledi.ai.config;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置RAG增强器
 *
 * @author wuledi
 * <a href="https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_retrievalaugmentationadvisor_incubating">...</a>
 * <a href="https://java2ai.com/docs/1.0.0-M6.1/tutorials/retriever/#_top">...</a>
 */
@Configuration
public class BailianRagCloudAdvisorConfig {
    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;

    /**
     * 配置RAG增强器
     *
     * @return RAG增强器
     */
    @Bean
    public Advisor bailianRagCloudAdvisor() {

        // 创建DashScopeApi实例
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(dashScopeApiKey).build();
        final String KNOWLEDGE_INDEX = "算法工程师"; // 知识库名称

        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(
                dashScopeApi,  // DashScopeApi实例
                DashScopeDocumentRetrieverOptions.builder().withIndexName(KNOWLEDGE_INDEX).build()
        ); // 创建DashScopeDocumentRetriever实例

        return RetrievalAugmentationAdvisor.builder() // 创建RAG增强器
                .documentRetriever(documentRetriever) // 设置文档检索器
                .build();
    }

}