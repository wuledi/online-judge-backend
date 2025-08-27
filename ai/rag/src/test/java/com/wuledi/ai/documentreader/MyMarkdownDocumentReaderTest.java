package com.wuledi.ai.documentreader;

import com.wuledi.RagApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = RagApplication.class)
class MyMarkdownDocumentReaderTest {
    @Resource
    MyMarkdownDocumentReader myMarkdownDocumentReader;

    /**
     * 加载 Markdown 文档
     */
    @Test
    void loadMarkdowns() {
        List<Document> allDocuments = myMarkdownDocumentReader.loadMarkdowns();
        for (Document allDocument : allDocuments) {
            System.out.println(allDocument.getText());
        }
    }
}