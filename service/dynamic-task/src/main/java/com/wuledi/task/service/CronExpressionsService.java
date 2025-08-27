package com.wuledi.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuledi.task.model.entity.CronExpressionsDO;
import com.wuledi.task.model.request.InsertCronExpressionRequest;
import com.wuledi.task.model.request.QueryCronExpressionPageRequest;
import com.wuledi.task.model.request.UpdateCronExpressionRequest;
import com.wuledi.task.model.dto.CronExpressionsDTO;


/**
 * @author wuledi
 * @description 针对表【cron_expressions(cron表达式表)】的数据库操作Service
 * @createDate 2025-03-20 19:25:17
 */
public interface CronExpressionsService extends IService<CronExpressionsDO> {

    /**
     * 通过taskName查询单条数据
     *
     * @param taskName 任务名称
     * @return 实例对象
     */
    CronExpressionsDTO getByTaskName(String taskName);

    /**
     * 分页查询多条数据
     *
     * @param queryCronExpressionPageRequest 查询条件
     * @return 分页结果对象
     */
    Page<CronExpressionsDTO> queryByPage(QueryCronExpressionPageRequest queryCronExpressionPageRequest);

    /**
     * 新增数据
     *
     * @param request 实例对象
     * @return 是否成功
     */
    Boolean insert(InsertCronExpressionRequest request);

    /**
     * 修改数据
     *
     * @param request 实例对象
     * @return 是否成功
     */
    Boolean update(UpdateCronExpressionRequest request);

    /**
     * 通过taskName删除数据
     *
     * @param id 任务名称
     * @return 是否成功
     */
    Boolean deleteById(Long id);

}
