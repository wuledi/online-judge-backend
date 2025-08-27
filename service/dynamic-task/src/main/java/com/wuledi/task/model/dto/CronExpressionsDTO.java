package com.wuledi.task.model.dto;

import lombok.Data;

/**
 * cron表达式表
 * @TableName cron_expressions
 */
@Data
public class CronExpressionsDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * cron表达式
     */
    private String cronExpression;
}