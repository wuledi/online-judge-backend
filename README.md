# 智能OJ判题平台后端

一个支持多语言在线编程、调试、判题的 OJ 平台，集成代码沙箱与 AI 辅助编程能力，采用多模块单体/微服务架构设计实现。

官网: [https://wuledi.com](https://wuledi.com/)

前端+后端：[https://github.com/wuledi/oj](https://github.com/wuledi/oj)

## 功能特点

- **安全认证与用户管理**：基于 Spring Security + JWT 实现完善的用户注册、登录、权限控制体系
- **代码沙箱**：基于 Docker 容器技术的强隔离代码执行环境，确保系统安全
- **多语言判题引擎**：支持 Java/Python/C/C++ 等语言的代码提交、编译、执行与判题
- **性能优化**：Redis 分布式缓存与 Caffeine 本地缓存多级缓存策略，Redisson 分布式锁保障并发安全
- **异步解耦**：kafka 实现判题流程异步化，提升系统吞吐量和响应速度
- **开放平台**：提供开放 API 接口与客户端 SDK，支持第三方集成，具备完善的 API 签名认证机制
- **AI 辅助编程**：集成 Spring AI Alibaba，实现智能对话解答编程问题，具备多步需求分析能力的智能体
- **微服务架构**：基于 Spring Cloud Alibaba 的微服务架构，包含服务注册发现、配置中心、API 网关等

## 技术栈

| 类别        | 技术组件                                               |
|:----------|:---------------------------------------------------|
| **核心框架**  | Spring Boot 3.5.5, Spring Cloud Alibaba 2023.0.3.3 |
| **安全认证**  | Spring Security, JWT                               |
| **数据持久化** | MySQL, MyBatis-Plus, Elasticsearch                 |
| **中间件**   | Redis , RabbitMQ , Nacos                           |
| **容器技术**  | Docker                                             |
| **AI 集成** | Spring AI Alibaba                                  |
| **服务治理**  | Spring Cloud Gateway, OpenFeign                    |
| **构建工具**  | Gradle                                             |

## 快速开始

### 前提条件

- JDK 21
- Gradle 8.14.3
- MySQL 8
- Redis
- Docker
- kafka
- Elasticsearch 8.18.1
- Nacos 2.5.1

### 运行项目

- 单体：启动provider

```plaintext
online-judge/
├── common/                # 公共模块
│   ├── core/              # 核心模块
│   ├── web/               # web模块
│   └── security/          # 认证模块
│
├── base/                  # 基础服务模块
│   ├── notification/      # 通知模块
│   └── storage/           # 存储模块
│ 
├── orm/                   # ORM模块
│   └── mybatis-plus/      # MyBatis-Plus模块
│ 
├── middleware/            # 中间件模块
│   ├── redis/             # Redis模块
│   └── kafaka/            # Kafka模块
│
├── service/               # 业务服务模块
│   ├── user/              # 用户服务
│   ├── chat/              # 聊天服务
│   ├── article/           # 文章服务
│   ├── dynamic-task/      # 动态任务服务
│   ├── interface-info/    # 接口信息服务
│   ├── code-sandbox/      # 代码沙箱服务
│   ├── question/          # OJ问题服务
│   ├── judge/             # OJ判题服务
│   └── meta-search/       # 搜索服务
│ 
├── sdk/                   # SDK模块
│   └── code-sandbox-sdk/  # 代码沙箱SDK模块
│
├── ai/                    # AI模块
│   ├── ai-invoke-example/ # AI调用示例模块
│   ├── dashscope/         # 灵积服务集成模块
│   ├── mcp/               # MCP模块
│   ├── tool/              # AI工具模块
│   ├── rag/               # 检索增强生成模块
│   ├── memory/            # 聊天记忆
│   └── manus/             # Manus模块
│
├── provider/              # 提供者模块: 单机模式
├── gateway/               # 网关层: 微服务模式
└── feign-api/             # RPC层
```