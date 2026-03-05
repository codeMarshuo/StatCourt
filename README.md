# NBA 球员数据统计平台

基于 NBA 中国官网 2024-2025 赛季季后赛球员数据，搭建标准化、高交互性的球员数据统计平台。

## 项目概述

本平台是 NBA 球员赛事数据服务平台，聚焦 NBA 全赛季、全赛事阶段的球员个人技术统计数据，为用户提供权威、实时、多维度、可定制的球员数据查询、筛选、分析、对比服务。

## 技术架构

### 技术栈

| 层级 | 技术选型 |
|------|---------|
| 前端 | React 18 + Ant Design + ECharts + Axios |
| 后端 | Spring Boot 3.x + Spring Cloud + MyBatis-Plus |
| 数据库 | MySQL 8.0 + Redis 7.x + Elasticsearch 8.x |
| 部署 | Docker + Kubernetes + Jenkins |

### 系统架构

采用前后端分离的云原生微服务架构，整体分为 5 层：

- **接入层**：CDN 静态加速、API 网关/负载均衡、WAF 防火墙
- **应用层**：前端 Web 应用、后台管理系统
- **服务层**：球员数据服务、赛季球队服务、数据采集同步服务、统计分析服务、系统权限服务
- **数据层**：MySQL 业务数据库、Redis 缓存数据库、Elasticsearch 搜索引擎
- **基础设施层**：容器化编排 K8s、监控告警体系、日志中心、定时任务调度

## 项目结构

```
NBA/
├── site/                          # 前端静态网站
│   ├── index.html                 # 首页
│   ├── data.html                  # 球员数据列表页
│   ├── login.html                 # 登录页
│   ├── css/
│   │   ├── styles.css             # 全局样式
│   │   ├── index.css              # 首页样式
│   │   ├── data.css               # 数据页样式
│   │   └── weblogin.css           # 登录页样式
│   ├── js/
│   │   └── main.js                # 主脚本
│   └── assets/                    # 静态资源
│       ├── NBA需求文档.md         # 产品需求文档
│       └── xuqiu.md               # 技术架构文档
│
├── backend/                       # 后端服务
│   ├── pom.xml                    # Maven 配置
│   └── src/
│       └── main/
│           ├── java/com/nba/stats/
│           │   ├── NbaStatsApplication.java
│           │   ├── common/         # 公共类
│           │   │   └── Result.java
│           │   ├── entity/         # 实体类
│           │   │   └── Season.java
│           │   ├── mapper/         # 数据访问层
│           │   ├── service/        # 业务逻辑层
│           │   ├── controller/     # 控制器层
│           │   ├── config/         # 配置类
│           │   └── util/           # 工具类
│           └── resources/
│               └── application.yml
│
├── database/                      # 数据库
│   └── init.sql                   # 数据库初始化脚本
│
└── .venv/                         # Python 虚拟环境（数据采集用）
```

## 数据库设计

### 核心表结构

| 表名 | 说明 |
|------|------|
| t_season | 赛季表 |
| t_team | 球队表 |
| t_player | 球员基础信息表 |
| t_player_season_stats | 球员赛季数据核心表 |
| t_sys_user | 系统用户表 |
| t_sys_role | 系统角色表 |
| t_sys_permission | 系统权限表 |
| t_sys_user_role | 用户角色关联表 |
| t_sys_role_permission | 角色权限关联表 |
| t_sys_operation_log | 系统操作日志表 |

### 初始化数据库

```bash
mysql -u root -p < database/init.sql
```

默认管理员账号：`admin` / `admin123`

## 快速开始

### 前端启动

```bash
cd site
# 方式一：直接用浏览器打开 index.html
# 方式二：使用 Python 启动本地服务器
python -m http.server 8000
```

访问 http://localhost:8000

### 后端启动

```bash
cd backend
# 修改 application.yml 中的数据库连接配置
mvn clean install
mvn spring-boot:run
```

后端服务地址：http://localhost:8080/api

## 核心功能

### 前端功能

1. **平台首页**：核心数据榜单、热门球员推荐、赛事联动区、资讯内容区
2. **球员数据列表页**：多维度筛选、灵活排序、精准搜索、数据导出
3. **球员详情页**：球员基础信息、核心数据概览、赛季数据切换、单场比赛数据
4. **数据对比分析页**：多球员数据对比、可视化图表、结果导出分享
5. **个人中心页**：我的收藏、我的订阅、我的历史记录、账号设置

### 后端接口

| 接口 | 说明 |
|------|------|
| GET /api/v1/season/list | 获取赛季列表 |
| GET /api/v1/team/list | 获取球队列表 |
| GET /api/v1/player/stats/list | 球员数据列表查询 |
| GET /api/v1/player/base/{playerId} | 球员基础信息详情 |
| GET /api/v1/player/stats/{playerId} | 球员赛季数据详情 |
| GET /api/v1/team/detail/{teamId} | 球队详情查询 |

## 安全策略

- **身份认证**：JWT + Spring Security 实现无状态身份认证
- **权限控制**：RBAC 三级权限模型
- **接口限流**：基于 Sentinel 实现接口限流
- **数据防护**：SQL 注入防护、XSS 跨站脚本防护、CSRF 跨站请求伪造防护
- **日志审计**：全量操作日志记录

## 部署架构

采用 Kubernetes 容器化集群部署：

- 前端静态资源：阿里云 OSS + CDN 加速
- 网关层：Nginx + Spring Cloud Gateway
- 应用服务层：核心服务多副本部署
- 数据库层：MySQL 一主两从架构
- 缓存层：Redis 三主三从集群架构

## 项目排期

| 阶段 | 工期 | 输出物 |
|------|------|--------|
| 需求确认阶段 | 3个工作日 | 需求文档终稿、需求评审纪要 |
| 设计阶段 | 5个工作日 | 产品原型图、UI视觉稿、交互说明文档 |
| 开发阶段 | 7个工作日 | 开发版本、接口文档、数据库设计文档 |
| 测试阶段 | 5个工作日 | 测试用例、测试报告、bug修复记录 |
| 上线验收阶段 | 2个工作日 | 验收报告、上线操作手册、运维文档 |

## 版权信息

Copyright © 2026 All rights reserved | StatCourt · NBA数据球场
