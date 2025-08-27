package com.wuledi.question.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 题目提交编程语言枚举
 *
 * @author wuledi
 */
@Getter
public enum QuestionSubmitLanguageEnum {

    JAVA("java", "java"),
    CPLUSPLUS("cpp", "cpp"),
    PYTHON("python", "python"),
    JAVASCRIPT("javascript", "javascript"),
    GOLANG("golang", "golang");

    private final String text; // 语言名称
    private final String value; // 语言标识符

    QuestionSubmitLanguageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) { // 为空直接返回 null
            return null;
        }
        // 遍历所有枚举，找到匹配的值
        for (QuestionSubmitLanguageEnum anEnum : QuestionSubmitLanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
