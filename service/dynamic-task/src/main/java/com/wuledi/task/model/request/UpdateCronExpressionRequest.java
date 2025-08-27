package com.wuledi.task.model.request;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 更新cron表达式请求体
 */
@TableName(value = "cron_expressions")
@Data
public class UpdateCronExpressionRequest {
    /**
     * 主键
     */
    private Long id;
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