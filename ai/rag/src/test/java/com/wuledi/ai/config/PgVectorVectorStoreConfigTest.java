package com.wuledi.ai.config;

import com.wuledi.RagApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {RagApplication.class})
class PgVectorVectorStoreConfigTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    /**
     * 测试向量数据库
     */
    @Test
    void pgVectorVectorStore() {
        // 创建文档
        List<Document> documents = List.of(
                new Document("知行录AI,OJ在线判题", Map.of("meta1", "meta1")),
                new Document("代码沙箱,在线编程", Map.of("meta2", "meta2")),
                new Document("测试", Map.of("meta2", "meta2")));

        pgVectorVectorStore.add(documents);  // 添加文档
        // 相似度查询
        List<Document> results = pgVectorVectorStore
                .similaritySearch( // 相似度查询
                        SearchRequest.builder()
                                .query("怎么学编程啊") // 查询语句
                                .topK(3) // 取前3条
                                .build());
        Assertions.assertNotNull(results); // 断言结果不为空
        System.out.println(results);
    }
}