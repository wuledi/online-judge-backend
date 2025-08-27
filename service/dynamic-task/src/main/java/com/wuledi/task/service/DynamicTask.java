package com.wuledi.task.service;

/**
 * 定时任务接口
 **/
public interface DynamicTask {
    void execute();

    String getTaskName();
}
