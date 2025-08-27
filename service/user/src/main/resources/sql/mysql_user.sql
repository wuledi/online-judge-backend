-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS information_wuledi;
USE information_wuledi;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(255) UNIQUE NOT NULL COMMENT '用户名',
    password    VARCHAR(256)        NOT NULL COMMENT '密码',
    nickname    VARCHAR(256)        NULL COMMENT '昵称',
    avatar      VARCHAR(512)        NULL COMMENT '头像',
    gender      TINYINT             NOT NULL DEFAULT 0 COMMENT '性别（0-未知，1-男，2-女）',
    phone       varchar(128)        NULL comment '电话',
    email       VARCHAR(255)        NULL COMMENT '邮箱',
    birthday    DATE                NULL COMMENT '生日',
    profile     TEXT                NULL COMMENT '简介',
    union_id    VARCHAR(256)        NULL COMMENT '微信ID',
    mp_open_id  VARCHAR(256)        NULL COMMENT '公众号OpenID',
    access_key  VARCHAR(256)        NULL COMMENT 'Access Key',
    secret_key  VARCHAR(256)        NULL COMMENT 'Secret Key',
    role        TINYINT             NOT NULL DEFAULT 0 COMMENT '角色（0-用户，1-管理员）',
    status      TINYINT             NOT NULL DEFAULT 0 COMMENT '状态(0-正常，1-冻结)',
    type        VARCHAR(256)        NULL COMMENT '类型',
    badge       VARCHAR(256)        NULL COMMENT '徽章',
    tags        varchar(1024)       NULL comment '标签 json 列表',
    login_time  DATETIME            NULL COMMENT '登录时间',
    create_time DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete   TINYINT             NOT NULL DEFAULT 0 COMMENT '是否删除',

    -- 索引优化
    INDEX idx_union_id (union_id),
    INDEX idx_email (email),
    INDEX idx_status_role (status, role),
    INDEX idx_create_time (create_time),
    INDEX idx_phone (phone),
    INDEX idx_is_delete (is_delete),
    INDEX idx_access_key (access_key)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

