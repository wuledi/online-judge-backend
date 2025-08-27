-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS information_wuledi;
USE information_wuledi;

-- 文章表
CREATE TABLE IF NOT EXISTS article
(
    id            bigint auto_increment comment 'id' primary key not null comment '文章id',
    title         varchar(512)                                   null comment '标题',
    content       text                                           null comment '内容',
    tags          varchar(1024)                                  null comment '标签列表（json 数组）',
    thumb_number  int      default 0                             not null comment '点赞数',
    favour_number int      default 0                             not null comment '收藏数',
    user_id       bigint                                         not null comment '创建用户 id',
    create_time   datetime default CURRENT_TIMESTAMP             not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP             not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                             not null comment '是否删除',
    index idx_user_id (user_id)
) comment '文章' collate = utf8mb4_unicode_ci;

-- 文章点赞表（硬删除）
CREATE TABLE IF NOT EXISTS article_thumb
(
    id          bigint auto_increment comment 'id' primary key not null comment '文章点赞id',
    article_id  bigint                                         not null comment '文章 id',
    user_id     bigint                                         not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP             not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP             not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_article_id (article_id),
    index idx_user_id (user_id)
) comment '文章点赞';

-- 文章收藏表（硬删除）
CREATE TABLE IF NOT EXISTS article_favour
(
    id          bigint auto_increment comment 'id' primary key not null comment '文章收藏id',
    article_id  bigint                                         not null comment '文章 id',
    user_id     bigint                                         not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP             not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP             not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_article_id (article_id),
    index idx_user_id (user_id)
) comment '文章收藏';
