package com.wuledi.judge.sandbox;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */

@Component
public class CodeSandboxFactory {
    private final Map<String, CodeSandbox> sandboxMap;

    // 自动注入所有实现（Spring会按条件加载Bean）
    public CodeSandboxFactory(List<CodeSandbox> sandboxes) {
        sandboxMap = sandboxes.stream()
                .collect(Collectors.toMap(
                        this::getSandboxType,  // 关键修改：使用注解值作为key
                        Function.identity()
                ));
    }

    /**
     * 通过注解获取沙箱类型标识
     * @param sandbox 沙箱实例
     * @return 注解中定义的type值
     * @throws IllegalArgumentException 如果找不到注解
     */
    private String getSandboxType(CodeSandbox sandbox) {
        Class<?> clazz = sandbox.getClass();
        SandboxType annotation = clazz.getAnnotation(SandboxType.class);

        if (annotation == null) {
            throw new IllegalStateException(
                    String.format("沙箱实现类 %s 缺少 @SandboxType 注解", clazz.getSimpleName())
            );
        }
        return annotation.value().toLowerCase();
    }

    /**
     * 创建代码沙箱
     *
     * @return 代码沙箱
     */
    public CodeSandbox getInstance(String type) {
        String key = type.toLowerCase();
        CodeSandbox sandbox = sandboxMap.get(key);

        if (sandbox == null) {
            throw new IllegalArgumentException(
                    String.format("未配置的沙箱类型: %s (可用类型: %s)",
                            type,
                            String.join(", ", sandboxMap.keySet()))
            );
        }
        return new CodeSandboxProxy(sandbox);
    }
}