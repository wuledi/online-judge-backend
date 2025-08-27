-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS information_wuledi;
USE information_wuledi;

-- 创建私聊消息表
CREATE TABLE IF NOT EXISTS private_message
(
    id           bigint(20)                                            NOT NULL COMMENT '私聊消息ID',
    from_user_id bigint(20)                                            NOT NULL COMMENT '发送者ID',
    to_user_id   bigint(20)                                            NOT NULL COMMENT '接收者ID',
    content      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
    sent_time    datetime                                              NOT NULL COMMENT '发送时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4 COMMENT = '私聊消息';