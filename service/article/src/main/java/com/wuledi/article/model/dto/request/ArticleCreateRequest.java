package com.wuledi.article.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @author wuledi
 */
@Data
public class ArticleCreateRequest implements Serializable {

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Length(min = 2,max = 100, message = "标题长度不能小于2,不能大于100")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    @Length(min = 10,max = 100000, message = "内容长度不能小于10,不能大于100000")
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    @Serial
    private static final long serialVersionUID = 1L;
}