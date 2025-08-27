package com.wuledi.task.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 插入cron表达式请求
 */
@Data
public class InsertCronExpressionRequest {

    /**
     * 任务名称
     */
    @NotBlank(message = "{taskName.notBlank}")
    @Length(min = 2, max = 100, message = "{taskName.length}")
    private String taskName;

    /**
     * cron表达式
     */
    @NotBlank(message = "{cronExpression.notBlank}")
    @Length(min = 6, max = 20, message = "{cronExpression.length}")
    private String cronExpression;
}