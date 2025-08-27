package com.wuledi.ai.splitter;


import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义基于 Token 的切词器
 */
@Component
public class MyTokenTextSplitter {

    /**
     * 切分文档
     * @param documents 文档列表
     * @return 切分后的文档列表
     */
    public List<Document> splitDocuments(List<Document> documents) {
        //  使用默认的切词器
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents); // 切分文档
    }

    /**
     * 自定义切词器
     * @param documents 文档列表
     * @return 切分后的文档列表
     */
    public List<Document> splitCustomized(List<Document> documents) {
        // 自定义切词器，切词规则：每个文档不超过 200 个 token，每个段落不超过 100 个 token，每个句子不超过 10 个 token，每个段落不超过 5000 个 token，每个段落不超过 10 个 token
        TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
        return splitter.apply(documents);
    }
}
