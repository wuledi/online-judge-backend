package com.wuledi.metasearch.model.enums;

import com.wuledi.common.constant.SearchConstant;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索类型枚举
 *
 */
@Getter
public enum SearchTypeEnum {

    ARTICLE("文章", SearchConstant.ARTICLE),
    USER("用户", SearchConstant.USER),
    PICTURE("图片", SearchConstant.PICTURE),
    QUESTION("题目", SearchConstant.QUESTION),
    ;

    private final String text;

    private final String value;

    SearchTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return List<String>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 值
     * @return SearchTypeEnum
     */
    public static SearchTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) { // 如果值为空
            return null;
        }
        for (SearchTypeEnum anEnum : SearchTypeEnum.values()) { // 遍历枚举
            if (anEnum.value.equals(value)) { // 如果值相等
                return anEnum;
            }
        }
        return null;
    }

}
