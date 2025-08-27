package com.wuledi.ai.transformer;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器
 */
@Component
public class MyQueryTransformer {

    private final QueryTransformer queryTransformer;

    public MyQueryTransformer(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 创建查询重写转换器
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    /**
     * 执行查询重写
     *
     * @param prompt 用户输入的查询
     * @return 重写后的查询
     */
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt); // 创建查询对象

        Query transformedQuery = queryTransformer.transform(query);   // 执行查询重写

        return transformedQuery.text(); //输出重写后的查询
    }
}
