package com.wuledi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuledi.task.model.entity.CronExpressionsDO;
import org.apache.ibatis.annotations.Select;

/**
* @author wuledi
* @description 针对表【cron_expressions(cron表达式表)】的数据库操作Mapper
* @createDate 2025-03-20 19:25:17
* @Entity com.wuledi.model.entity.CronExpressions
*/
public interface CronExpressionsMapper extends BaseMapper<CronExpressionsDO> {

    /**
     * 通过taskName查询单条数据
     *
     * @param taskName 任务名称
     * @return 实例对象
     */
    @Select("SELECT * FROM cron_expressions WHERE task_name = #{taskName}")
    CronExpressionsDO getByTaskName(String taskName);

}




