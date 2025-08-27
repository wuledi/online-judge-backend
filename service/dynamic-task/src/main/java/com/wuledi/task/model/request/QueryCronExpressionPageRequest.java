package com.wuledi.task.model.request;


import com.wuledi.common.param.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询cron表达式表请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryCronExpressionPageRequest extends PageRequest {
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