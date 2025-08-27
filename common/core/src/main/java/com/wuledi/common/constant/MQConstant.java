package com.wuledi.common.constant;

/**
 * 队列名称常量
 */
public interface MQConstant {

    // 交换机名称常量（建议包含模块、功能、类型）
    String USER_REGISTRATION_MAIL_DIRECT_EXCHANGE = "user.registration.mail.direct"; // 用户注册邮件直接交换机

    String OJ_QUESTION_DEBUG_DIRECT_EXCHANGE = "oj.question.debug.direct"; // oj代码调试直接交换机

    // 队列名称常量（建议：动作.模块.功能）
    String SEND_MAIL_QUEUE = "send.user.registration.mail.queue"; // 发送注册邮件队列
    String PROCESS_MAIL_CONFIRM_QUEUE = "process.user.registration.mail.confirm.queue"; // 处理邮件确认队列


    // online judge
    // 判题队列配置
    String OJ_JUDGE_TASK_DIRECT_EXCHANGE = "oj.judge.task.direct"; // oj题目提交直接交换机
    String OJ_JUDGE_TASK_QUEUE = "oj.judge.task.queue"; // 判题任务队列

    // 死信队列配置
    String OJ_DLX_EXCHANGE = "oj.dlx.exchange"; // 死信交换机
    String OJ_DLX_QUEUE = "oj.dlx.queue"; // 死信队列
    String OJ_DLX_ROUTING_KEY = "oj.dlx.routingKey"; // 死信路由键

    // 代码调试配置
    String CODE_DEBUG_QUEUE = "oj.code.debug.queue"; // 代码调试队列



    // 路由键常量（建议：模块.功能.动作）
    String USER_REGISTRATION_MAIL_SEND_KEY = "user.registration.mail.send"; // 发送注册邮件路由键
    String USER_REGISTRATION_MAIL_CONFIRM_KEY = "user.registration.mail.confirm"; // 确认注册邮件路由键
    String OJ_QUESTION_SUBMIT_KEY = "oj.question.submit"; // oj题目提交路由键
    String OJ_QUESTION_DEBUG_KEY = "oj.question.debug"; // oj代码调试路由键


}