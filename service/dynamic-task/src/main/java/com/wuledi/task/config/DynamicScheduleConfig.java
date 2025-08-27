package com.wuledi.task.config;

import com.wuledi.task.service.CronExpressionsService;
import com.wuledi.task.service.DynamicTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * 动态定时任务配置
 *
 * @author wuledi
 */
@Configuration
@EnableScheduling// 启用定时任务
@Slf4j
public class DynamicScheduleConfig implements SchedulingConfigurer {

    // 动态Cron表达式
    private final CronExpressionsService cronExpressionsService;
    // 动态任务
    private final List<DynamicTask> tasks;


    /**
     * 构造函数
     *
     * @param cronExpressionsService 动态Cron表达式
     * @param tasks                 动态任务
     */
    public DynamicScheduleConfig(CronExpressionsService cronExpressionsService, List<DynamicTask> tasks) {
        this.cronExpressionsService = cronExpressionsService;
        this.tasks = tasks;
    }

    /**
     * 配置定时任务
     *
     * @param taskRegistrar 定时任务注册器
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar // 任务注册器
                .setScheduler(Executors.newScheduledThreadPool(10));  // 10个线程池

        tasks.forEach(task -> { // 遍历任务列表
            log.info("配置定时任务: {}", task.getTaskName());
            taskRegistrar // 添加任务触发器
                    .addTriggerTask( // 添加任务触发器,可以添加多个任务
                            task::execute, // 执行的任务方法
                            triggerContext -> { // 任务触发器
                                String cron = cronExpressionsService.getByTaskName(task.getTaskName()).getCronExpression(); // 获取CRON表达式
                                return new CronTrigger(cron) // 返回CronTrigger对象
                                        .nextExecution(triggerContext); // 返回下一次执行时间
                            }
                    );
        });

    }
}