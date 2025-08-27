package com.wuledi.task.contrroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.task.model.converter.CronExpressionsConvert;
import com.wuledi.task.model.dto.CronExpressionsDTO;
import com.wuledi.task.model.request.InsertCronExpressionRequest;
import com.wuledi.task.model.request.QueryCronExpressionPageRequest;
import com.wuledi.task.model.request.UpdateCronExpressionRequest;
import com.wuledi.task.model.vo.CronExpressionsVO;
import com.wuledi.task.service.CronExpressionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * CRON表达式控制器
 */
@Tag(name = "CronExpressionsController")
@Slf4j
@RestController
@RequestMapping("/api/cronExpressions")
public class CronExpressionsController {

    @Resource
    private CronExpressionsService cronExpressionsService;

    @Resource
    private CronExpressionsConvert cronExpressionsConvert;

    /**
     * 根据任务名称查询单条数据
     *
     * @param taskName 任务名称
     * @return CronExpressionsVO 对象
     */
    @Operation(summary = "通过任务名称查询单条数据")
    @GetMapping("/{taskName}")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<CronExpressionsVO> getByTaskName(
            @Parameter(description = "任务名称", required = true)
            @PathVariable String taskName) {
        CronExpressionsDTO cronExpressionsDTO = cronExpressionsService.getByTaskName(taskName);
        CronExpressionsVO cronExpressionsVO = cronExpressionsConvert.toVo(cronExpressionsDTO);
        return ResultUtils.success(cronExpressionsVO);
    }

    /**
     * 新增CRON表达式
     *
     * @param request 新增请求对象
     * @return 是否成功
     */
    @Operation(summary = "新增CRON表达式")
    @PostMapping
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> insert(
            @Parameter(description = "新增请求对象", required = true)
            @RequestBody @Valid InsertCronExpressionRequest request) {
        return ResultUtils.success(cronExpressionsService.insert(request));
    }

    /**
     * 修改CRON表达式
     *
     * @param request 修改请求对象
     * @return 是否成功
     */
    @Operation(summary = "修改CRON表达式")
    @PutMapping
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> update(
            @Parameter(description = "修改请求对象", required = true)
            @RequestBody @Valid UpdateCronExpressionRequest request) {
        return ResultUtils.success(cronExpressionsService.update(request));
    }

    /**
     * 通过ID删除CRON表达式
     *
     * @param id 主键ID
     * @return 是否成功
     */
    @Operation(summary = "通过ID删除CRON表达式")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteById(
            @Parameter(description = "主键ID", required = true)
            @PathVariable Long id) {
        return ResultUtils.success(cronExpressionsService.deleteById(id));
    }

    /**
     * 分页查询CRON表达式数据
     *
     * @param queryCronExpressionPageRequest 分页查询条件
     * @return 分页结果对象
     */
    @Operation(summary = "分页查询CRON表达式数据")
    @PostMapping("/queryPage")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Page<CronExpressionsVO>> queryByPage(
            @Parameter(description = "分页查询条件", required = true)
            @RequestBody @Valid QueryCronExpressionPageRequest queryCronExpressionPageRequest) {

        Page<CronExpressionsDTO> cronExpressionsPage = cronExpressionsService.queryByPage(queryCronExpressionPageRequest);
        Page<CronExpressionsVO> cronExpressionsVOPage = new Page<>(cronExpressionsPage.getCurrent(), cronExpressionsPage.getSize(), cronExpressionsPage.getTotal());
        cronExpressionsVOPage.setRecords(cronExpressionsPage.getRecords().stream().map(cronExpressions -> cronExpressionsConvert.toVo(cronExpressions)).toList());
        return ResultUtils.success(cronExpressionsVOPage);
    }
}