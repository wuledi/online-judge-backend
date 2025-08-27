package com.wuledi.ai.config;

import com.wuledi.ai.documentreader.MyMarkdownDocumentReader;
import com.wuledi.ai.splitter.MyTokenTextSplitter;
import com.wuledi.ai.transformer.MyKeywordMetadataEnricher;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 自定义向量库: 在内存中保存向量
 *
 * @author wuledi
 */
//@Configuration  // todo 使用时，取消注释
public class MyVectorStoreConfig {

    @Resource
    private MyMarkdownDocumentReader myMarkdownDocumentReader; // 文档读取器

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter; // 切词器

    @Resource
    private MyKeywordMetadataEnricher myKeywordMetadataEnricher; // 元信息增强器

    @Bean
    VectorStore myVectorStore(EmbeddingModel zhiPuAiEmbeddingModel) {
        // 创建向量库
        SimpleVectorStore simpleVectorStore = SimpleVectorStore // 创建向量库
                .builder(zhiPuAiEmbeddingModel) // 使用dashscopeEmbeddingModel
                .build();

        // 加载文档
        List<Document> documentList = myMarkdownDocumentReader.loadMarkdowns();
        // 自主切词: 不推荐
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documentList); // 切词
        List<Document> documentList1 = myKeywordMetadataEnricher.enrichDocuments(documentList); // 为文档补充元信息
        simpleVectorStore.add(documentList1); // 添加文档
        return simpleVectorStore;
    }
}
