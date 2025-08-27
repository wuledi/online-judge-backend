package com.wuledi.ai.factory;


import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建上下文查询增强器的工厂
 */
public class ContextualQueryAugmenterFactory {

    /**
     * 创建上下文查询增强器的工厂
     *
     * @return 上下文查询增强器
     */
    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答算法相关的问题，别的没办法帮到您哦，
                有问题可以联系客服 https://wuledi.com
                """);
        return ContextualQueryAugmenter.builder() // 创建上下文查询增强器
                .allowEmptyContext(false) // 是否允许空上下文
                .emptyContextPromptTemplate(emptyContextPromptTemplate) // 空上下文的提示模板
                .build(); // 构建
    }
}
