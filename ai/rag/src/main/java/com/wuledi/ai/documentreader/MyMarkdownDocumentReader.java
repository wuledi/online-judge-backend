package com.wuledi.ai.documentreader;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载 Markdown 文档
 * <a href="https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#_markdown">...</a>
 */
@Component
@Slf4j
public class MyMarkdownDocumentReader {

    private final ResourcePatternResolver resourcePatternResolver; // 资源模式解析器

    MyMarkdownDocumentReader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 Markdown 文档
     *
     * @return 文档列表
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            // 这里可以修改为你要加载的多个 Markdown 文件的路径模式
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                String status = "";
                if (fileName != null) {
                    // 提取文件名中的状态信息
                    status = fileName.substring(fileName.lastIndexOf("-") + 1, fileName.lastIndexOf(".") - 1);
                }
                if (fileName == null) {
                    continue;
                }

                // 创建 Markdown 文档读取器
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true) // 分割线创建文档
                        .withIncludeCodeBlock(false) // 不包含代码块
                        .withIncludeBlockquote(false) // 不包含引用块
                        .withAdditionalMetadata("filename", fileName) // 额外的元数据
                        .withAdditionalMetadata("status", status) // 额外的元数据
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config); // 创建 Markdown 文档读取器
                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }
}

