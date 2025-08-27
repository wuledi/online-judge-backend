package com.wuledi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.task.mapper.CronExpressionsMapper;
import com.wuledi.task.model.converter.CronExpressionsConvert;
import com.wuledi.task.model.dto.CronExpressionsDTO;
import com.wuledi.task.model.entity.CronExpressionsDO;
import com.wuledi.task.model.request.InsertCronExpressionRequest;
import com.wuledi.task.model.request.QueryCronExpressionPageRequest;
import com.wuledi.task.model.request.UpdateCronExpressionRequest;
import com.wuledi.task.service.CronExpressionsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuledi
 * @description 针对表【cron_expressions(cron表达式表)】的数据库操作Service实现
 * @createDate 2025-03-20 19:25:17
 */
@Service
public class CronExpressionsServiceImpl extends ServiceImpl<CronExpressionsMapper, CronExpressionsDO>
        implements CronExpressionsService {
    @Resource
    private CronExpressionsMapper cronExpressionsMapper;

    @Resource
    private CronExpressionsConvert cronExpressionsConvert;

    /**
     * 通过taskName查询单条数据
     *
     * @param taskName 任务名称
     * @return 实例对象
     */
    @Override
    public CronExpressionsDTO getByTaskName(String taskName) {
        ThrowUtils.throwIf(taskName == null, ErrorCode.PARAMS_ERROR, "任务名称不能为空");
        CronExpressionsDO cronExpressionsDO = cronExpressionsMapper.getByTaskName(taskName);
        ThrowUtils.throwIf(cronExpressionsDO.getId() == null, ErrorCode.NOT_FOUND_ERROR, "任务不存在");
        return cronExpressionsConvert.toDto(cronExpressionsDO);
    }

    /**
     * 分页查询多条数据
     *
     * @param queryCronExpressionPageRequest 查询条件
     * @return 分页结果对象
     */
    @Override
    public Page<CronExpressionsDTO> queryByPage(QueryCronExpressionPageRequest queryCronExpressionPageRequest) {
        ThrowUtils.throwIf(queryCronExpressionPageRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long pageNumber = queryCronExpressionPageRequest.getPageNumber(); // 获取页码
        long pageSize = queryCronExpressionPageRequest.getPageSize(); // 获取每页大小
        Page<CronExpressionsDTO> page = new Page<>(pageNumber, pageSize); // 实例化分页对象
        // 构建查询条件
        QueryWrapper<CronExpressionsDO> queryWrapper = new QueryWrapper<>();
        if (queryCronExpressionPageRequest.getId() != null) { // 获取主键
            queryWrapper.eq("id", queryCronExpressionPageRequest.getId());
        }
        if (queryCronExpressionPageRequest.getTaskName() != null) { // 获取任务名称
            queryWrapper.eq("task_name", queryCronExpressionPageRequest.getTaskName());
        }
        if (queryCronExpressionPageRequest.getCronExpression() != null) { // 获取cron表达式
            queryWrapper.eq("cron_expression", queryCronExpressionPageRequest.getCronExpression());
        }
        // 执行分页查询
        Page<CronExpressionsDO> cronExpressionsPage = cronExpressionsMapper
                .selectPage(new Page<>(pageNumber, pageSize), queryWrapper);

        // 拷贝属性
        List<CronExpressionsDO> records = cronExpressionsPage.getRecords(); // 获取查询结果
        List<CronExpressionsDTO> cronExpressionsDTOList = records
                .stream()
                .map(cronExpressionsDO -> cronExpressionsConvert.toDto(cronExpressionsDO))
                .toList(); // 转换为VO对象列表
        page.setRecords(cronExpressionsDTOList); // 设置分页结果
        return page;
    }

    /**
     * 新增数据
     *
     * @param request 实例对象
     * @return 实例对象
     */
    @Override
    public Boolean insert(InsertCronExpressionRequest request) {
        validateInsert(request); // 校验新增参数
        CronExpressionsDO cronExpressionsDO =  CronExpressionsDO.builder()
                .taskName(request.getTaskName())
                .cronExpression(request.getCronExpression())
                .build();
        boolean res = this.save(cronExpressionsDO); // 保存数据
        ThrowUtils.throwIf(!res, ErrorCode.SYSTEM_ERROR, "新增失败");
        return true;
    }


    /**
     * 修改数据
     *
     * @param request 实例对象
     * @return 实例对象
     */
    @Override
    public Boolean update(UpdateCronExpressionRequest request) {
        validateUpdate(request); // 校验修改参数
        CronExpressionsDO cronExpressionsDO = CronExpressionsDO.builder()
                .id(request.getId())
                .taskName(request.getTaskName())
                .build();
        boolean res = this.updateById(cronExpressionsDO); // 修改数据
        ThrowUtils.throwIf(!res, ErrorCode.SYSTEM_ERROR, "修改失败");
        return true;
    }

    /**
     * 通过taskName删除数据
     *
     * @param id 任务名称
     * @return 是否成功
     */
    @Override
    public Boolean deleteById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "主键异常");
        boolean res = this.removeById(id); // 删除数据
        ThrowUtils.throwIf(!res, ErrorCode.SYSTEM_ERROR, "删除失败");
        return true;
    }

    /**
     * 校验新增参数
     *
     * @param request 新增参数
     */
    private void validateInsert(InsertCronExpressionRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(request.getTaskName() == null, ErrorCode.PARAMS_ERROR, "任务名称不能为空");
        ThrowUtils.throwIf(request.getCronExpression() == null, ErrorCode.PARAMS_ERROR, "cron表达式不能为空");
    }

    /**
     * 校验修改参数
     *
     * @param request 修改参数
     */
    private void validateUpdate(UpdateCronExpressionRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(request.getId() <= 0, ErrorCode.PARAMS_ERROR, "主键异常");
        ThrowUtils.throwIf(request.getTaskName() == null, ErrorCode.PARAMS_ERROR, "任务名称不能为空");
        ThrowUtils.throwIf(request.getCronExpression() == null, ErrorCode.PARAMS_ERROR, "cron表达式不能为空");
    }
}




