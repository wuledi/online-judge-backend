create database if not exists information_wuledi;
-- 切换库
use information_wuledi;
CREATE TABLE if not exists cron_expressions
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
    task_name       VARCHAR(255)                      NOT NULL UNIQUE not null comment '任务名称',
    cron_expression VARCHAR(255)                      NOT NULL not null comment 'cron表达式',
    index idx_task_name (task_name)
) comment 'cron表达式表';