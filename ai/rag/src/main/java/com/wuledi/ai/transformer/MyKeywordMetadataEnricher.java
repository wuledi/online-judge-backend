package com.wuledi.ai.transformer;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 AI 的文档元信息增强器（为文档补充元信息）
 */
@Component
public class MyKeywordMetadataEnricher {

    @Resource
    private ChatModel zhiPuAiChatModel; // 模型

    /**
     * 为文档补充元信息
     *
     * @param documents 文档列表
     * @return 增强后的文档列表
     */
    public List<Document> enrichDocuments(List<Document> documents) {
        // 创建元信息增强器: 模型,每次调用模型的次数
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(zhiPuAiChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }
}
