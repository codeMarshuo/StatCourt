# NBA 球员数据统计平台 技术架构文档

表格







| 文档版本 |  编写日期  |   编写人   |                           文档说明                           |
| :------: | :--------: | :--------: | :----------------------------------------------------------: |
|   V1.0   | 2025-XX-XX | 架构设计组 | 基于 NBA 中国官网球员数据统计业务，完成全链路技术架构设计，包含架构分层、路由 API、数据库设计、安全策略等核心内容 |

## 1. 文档概述

### 1.1 文档目的

本文档详细定义 NBA 球员数据统计平台的技术架构体系，明确系统分层、模块边界、接口规范、数据模型与安全规则，为开发、测试、运维及后续迭代提供统一的技术标准和落地依据。

### 1.2 适用范围

本架构适用于 NBA 球员数据统计平台的 Web 端产品，覆盖前端展示、后端服务、数据存储、数据采集全流程，适用于研发、运维、产品等相关团队。

### 1.3 术语定义

表格







|   术语   |           全称            |            说明            |
| :------: | :-----------------------: | :------------------------: |
|  常规赛  |      Regular Season       |  NBA 常规赛季赛事数据维度  |
|  季后赛  |         Playoffs          |   NBA 季后赛赛事数据维度   |
| 球员数据 |       Player Stats        | 球员单赛季赛事核心统计指标 |
|   RBAC   | Role-Based Access Control |   基于角色的权限访问控制   |
|   JWT    |      JSON Web Token       |     轻量级身份认证凭证     |

## 2. 系统整体架构设计

### 2.1 架构设计目标

1. **高可用性**：核心服务 99.9% 可用性，数据查询接口响应时间 < 200ms
2. **数据一致性**：赛事数据与官方数据源同步延迟 < 1 小时，支持手动实时同步
3. **可扩展性**：支持新增赛事类型、数据维度、统计分析功能的低代码扩展
4. **安全性**：全链路防注入、防篡改、权限管控，保障数据与系统安全
5. **易维护性**：模块化分层设计，代码职责单一，文档与代码同步迭代

### 2.2 整体分层架构

采用前后端分离的云原生微服务架构，整体分为 5 层，架构如下：











预览

查看代码

接入层应用层服务层数据层基础设施层CDN静态加速API网关/负载均衡WAF防火墙前端Web应用后台管理系统球员数据服务赛季球队服务数据采集同步服务统计分析服务系统权限服务MySQL业务数据库Redis缓存数据库Elasticsearch搜索引擎离线数据备份容器化编排K8s监控告警体系日志中心定时任务调度

```
graph TD
    A[接入层] --> B[应用层]
    B --> C[服务层]
    C --> D[数据层]
    D --> E[基础设施层]
    
    A[接入层] --> A1[CDN静态加速]
    A --> A2[API网关/负载均衡]
    A --> A3[WAF防火墙]
    
    B[应用层] --> B1[前端Web应用]
    B --> B2[后台管理系统]
    
    C[服务层] --> C1[球员数据服务]
    C --> C2[赛季球队服务]
    C --> C3[数据采集同步服务]
    C --> C4[统计分析服务]
    C --> C5[系统权限服务]
    
    D[数据层] --> D1[MySQL业务数据库]
    D --> D2[Redis缓存数据库]
    D --> D3[Elasticsearch搜索引擎]
    D --> D4[离线数据备份]
    
    E[基础设施层] --> E1[容器化编排K8s]
    E --> E2[监控告警体系]
    E --> E3[日志中心]
    E --> E4[定时任务调度]
```

接入层应用层服务层数据层基础设施层CDN静态加速API网关/负载均衡WAF防火墙前端Web应用后台管理系统球员数据服务赛季球队服务数据采集同步服务统计分析服务系统权限服务MySQL业务数据库Redis缓存数据库Elasticsearch搜索引擎离线数据备份容器化编排K8s监控告警体系日志中心定时任务调度



你的 AI 助手，助力每日工作学习

### 2.3 核心技术栈选型

表格







| 架构层级 |                   技术选型                    |                       选型说明                       |
| :------: | :-------------------------------------------: | :--------------------------------------------------: |
|  前端层  |    React 18 + Ant Design + ECharts + Axios    | 组件化开发，适配数据表格、可视化图表、多维度筛选场景 |
|  接入层  |   Nginx + Spring Cloud Gateway + 阿里云 CDN   |      负载均衡、路由转发、接口限流、静态资源加速      |
| 后端服务 | Spring Boot 3.x + Spring Cloud + MyBatis-Plus |       微服务架构，快速开发，ORM 简化数据库操作       |
| 数据采集 |       Python Scrapy + XXL-Job 定时调度        |    自动化爬取官方数据源，支持全量 / 增量数据同步     |
| 数据存储 |   MySQL 8.0 + Redis 7.x + Elasticsearch 8.x   |      关系型数据存储、热点数据缓存、全文检索加速      |
| 运维部署 |         Docker + Kubernetes + Jenkins         |       容器化部署，CI/CD 自动化发布，弹性扩缩容       |
| 安全防护 |       JWT + Spring Security + Sentinel        |      身份认证、权限管控、接口限流熔断、防刷防护      |

### 2.4 核心业务流程

1. **数据同步流程**：定时任务触发 → 数据采集服务爬取官方数据源 → 数据清洗与校验 → 写入业务数据库 → 更新缓存
2. **用户查询流程**：用户前端发起请求 → CDN / 网关转发 → 鉴权与限流校验 → 业务服务处理 → 优先读取缓存 → 无缓存查询数据库 → 数据格式化返回前端 → 前端渲染展示
3. **后台管理流程**：管理员登录 → 身份认证与权限校验 → 操作管理接口 → 数据增删改查 → 记录操作日志 → 同步更新缓存

## 3. 核心功能模块设计

### 3.1 数据采集与同步模块

- 核心职责：负责 NBA 官方球员数据的爬取、清洗、校验、入库，保障数据的准确性和时效性

- 核心功能：

  1. 全量数据同步：赛季初 / 赛季结束全量拉取球员、球队、赛季基础数据
  2. 增量数据同步：赛事结束后小时级增量更新球员赛事统计数据
  3. 数据校验：重复数据过滤、异常数据拦截、数据格式标准化处理
  4. 同步日志：记录每次同步的成功 / 失败条数、异常信息，支持同步重试

  

### 3.2 赛季管理模块

- 核心职责：维护赛季基础信息，管理不同赛季、不同赛事类型的数据维度
- 核心功能：赛季信息增删改查、赛季状态管理（未开始 / 进行中 / 已结束）、赛季数据归档、赛季维度数据筛选

### 3.3 球队管理模块

- 核心职责：维护 NBA 球队基础信息，建立球员与球队的所属关联
- 核心功能：球队基础信息管理、球队所属联盟 / 分区管理、球队球员列表查询、球队维度数据统计

### 3.4 球员基础信息管理模块

- 核心职责：维护球员全量基础档案信息
- 核心功能：球员基础信息增删改查、球员生涯信息管理、球员详情查询、球员多条件检索

### 3.5 球员赛事数据管理模块

- 核心职责：核心业务模块，管理球员单赛季常规赛 / 季后赛的全维度统计数据
- 核心功能：球员数据多维度分页查询、排名筛选、数据排序、单球员多赛季数据对比、数据导出
- 核心数据维度：比赛场次、出场时间、得分、篮板、助攻、抢断、盖帽、投篮命中率、三分命中数、三分命中率、罚球命中率

### 3.6 统计分析模块

- 核心职责：基于基础数据实现多维度可视化统计分析
- 核心功能：球员数据排行可视化、赛季数据趋势分析、球员能力雷达图、球队数据对比分析

### 3.7 系统管理与权限模块

- 核心职责：保障系统安全，管理用户、角色、权限与操作审计
- 核心功能：用户管理、角色管理、权限分配、操作日志审计、系统配置管理

## 4. 路由定义与 API 接口设计

### 4.1 前端路由定义

采用 History 路由模式，路由定义如下：

表格







|         路由路径         |    页面名称    |    权限要求    |                          页面说明                          |
| :----------------------: | :------------: | :------------: | :--------------------------------------------------------: |
|            /             |      首页      |      公开      |         平台入口，展示赛季热门数据、球员排行 TOP10         |
|      /player/stats       | 球员数据列表页 |      公开      | 核心页面，展示全量球员赛季数据，支持多维度筛选、排序、分页 |
| /player/detail/:playerId |   球员详情页   |      公开      |         展示球员基础信息、生涯数据、单赛季详细数据         |
|         /season          |   赛季列表页   |      公开      |          展示所有赛季信息，支持按赛季切换数据维度          |
|          /team           |   球队列表页   |      公开      |          展示所有球队信息，支持按球队筛选球员数据          |
|   /team/detail/:teamId   |   球队详情页   |      公开      |        展示球队基础信息、球队球员列表、球队赛季数据        |
|       /admin/login       |   后台登录页   |      公开      |                     管理员账号密码登录                     |
|          /admin          |    后台首页    |   管理员权限   |                 后台数据概览、同步任务监控                 |
|      /admin/player       |  球员数据管理  |   管理员权限   |             球员信息与数据的增删改查、手动同步             |
|       /admin/team        |    球队管理    |   管理员权限   |                        球队信息管理                        |
|      /admin/season       |    赛季管理    |   管理员权限   |                        赛季信息管理                        |
|      /admin/system       |    系统管理    | 超级管理员权限 |                 用户、角色、权限、日志管理                 |

### 4.2 后端 API 接口设计

遵循 RESTful API 设计规范，统一返回格式：

json











```
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 17XXXXXXXXX
}
```

错误码规范：200 成功，400 参数错误，401 未登录，403 权限不足，404 资源不存在，500 服务端错误

#### 4.2.1 公开接口（无需鉴权）

表格







|     接口名称     | 请求方式 |            接口路径             |                           请求参数                           |                           返回数据                           |               接口说明               |
| :--------------: | :------: | :-----------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------: |
|   获取赛季列表   |   GET    |       /api/v1/season/list       |                              无                              |            赛季 ID、赛季年份、赛季类型、赛季状态             |           全量赛季信息查询           |
|   获取球队列表   |   GET    |        /api/v1/team/list        |                   联盟 (可选)、分区 (可选)                   |           球队 ID、球队名称、城市、logo、所属联盟            |           全量球队信息查询           |
| 球员数据列表查询 |   GET    |    /api/v1/player/stats/list    | 赛季 ID (必选)、赛季类型 (必选)、球队 ID (可选)、页码、页长、排序字段、排序方式 | 分页列表：排名、球员 ID、球员姓名、球队、场次、出场时间、得分、篮板、助攻等全量统计字段 | 核心数据查询接口，支持多条件筛选排序 |
| 球员基础信息详情 |   GET    | /api/v1/player/base/{playerId}  |                      球员 ID (路径参数)                      | 球员 ID、姓名、英文名、国籍、出生日期、身高体重、位置、生涯信息 |           球员基础档案查询           |
| 球员赛季数据详情 |   GET    | /api/v1/player/stats/{playerId} |              球员 ID (路径参数)、赛季 ID (可选)              |                   球员多赛季全维度统计数据                   |          单球员赛季数据查询          |
|   球队详情查询   |   GET    |  /api/v1/team/detail/{teamId}   |                      球队 ID (路径参数)                      |                  球队基础信息、球队球员列表                  |             球队详情查询             |

#### 4.2.2 管理接口（需管理员鉴权）

表格







|      接口名称       | 请求方式 |               接口路径               |                 请求参数                  |  权限要求  |       接口说明       |
| :-----------------: | :------: | :----------------------------------: | :---------------------------------------: | :--------: | :------------------: |
|  手动触发数据同步   |   POST   |      /api/v1/admin/sync/trigger      | 同步类型 (全量 / 增量)、赛季 ID、赛季类型 | 数据管理员 | 手动触发官方数据同步 |
| 球员数据新增 / 编辑 |   POST   |   /api/v1/admin/player/stats/save    |           球员赛季全量统计数据            | 数据管理员 |   手动维护球员数据   |
|    球员数据删除     |  DELETE  |   /api/v1/admin/player/stats/{id}    |            数据 ID (路径参数)             | 数据管理员 |     球员数据删除     |
| 赛季信息新增 / 编辑 |   POST   |      /api/v1/admin/season/save       |               赛季基础信息                | 超级管理员 |     赛季信息维护     |
| 球队信息新增 / 编辑 |   POST   |       /api/v1/admin/team/save        |               球队基础信息                | 超级管理员 |     球队信息维护     |
|    用户列表查询     |   GET    |    /api/v1/admin/system/user/list    |         页码、页长、用户名 (可选)         | 超级管理员 |     系统用户查询     |
|    角色权限分配     |   POST   | /api/v1/admin/system/role/permission |           角色 ID、权限 ID 列表           | 超级管理员 |     角色权限配置     |
|    操作日志查询     |   GET    |    /api/v1/admin/system/log/list     |       页码、页长、操作人、操作时间        | 超级管理员 |     系统操作审计     |

## 5. 数据库模型设计

### 5.1 数据库设计原则

1. 遵循第三范式 (3NF)，减少数据冗余，保证数据一致性
2. 核心表必须有主键、创建时间、更新时间、逻辑删除标识
3. 高频查询字段建立合适索引，避免过度索引
4. 字段类型与长度合理设计，适配业务数据范围
5. 所有字段添加注释，保证表结构可读性与可维护性

### 5.2 核心表结构概览

表格







|         表名          |     表说明     |                        核心用途                         |
| :-------------------: | :------------: | :-----------------------------------------------------: |
|       t_season        |     赛季表     |                  存储 NBA 赛季基础信息                  |
|        t_team         |     球队表     |                  存储 NBA 球队基础档案                  |
|       t_player        | 球员基础信息表 |                  存储球员基础档案信息                   |
| t_player_season_stats | 球员赛季数据表 | 核心业务表，存储球员单赛季常规赛 / 季后赛全维度统计数据 |
|      t_sys_user       |   系统用户表   |                 存储后台管理员账号信息                  |
|      t_sys_role       |   系统角色表   |                    存储系统角色信息                     |
|   t_sys_permission    |   系统权限表   |               存储系统接口 / 菜单权限信息               |
|    t_sys_user_role    | 用户角色关联表 |               建立用户与角色的多对多关联                |
| t_sys_role_permission | 角色权限关联表 |               建立角色与权限的多对多关联                |
|  t_sys_operation_log  | 系统操作日志表 |             记录后台所有操作行为，用于审计              |

### 5.3 完整 SQL 建表语句

sql











```sql
-- 1. 赛季表
CREATE TABLE `t_season` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `season_year` varchar(20) NOT NULL COMMENT '赛季年份，如2024-2025',
  `season_type` tinyint NOT NULL COMMENT '赛季类型：1-常规赛，2-季后赛',
  `season_name` varchar(50) NOT NULL COMMENT '赛季名称',
  `start_time` date DEFAULT NULL COMMENT '赛季开始时间',
  `end_time` date DEFAULT NULL COMMENT '赛季结束时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '赛季状态：0-未开始，1-进行中，2-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_season_year_type` (`season_year`,`season_type`,`is_deleted`) COMMENT '赛季年份+类型唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='NBA赛季表';

-- 2. 球队表
CREATE TABLE `t_team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_code` varchar(30) NOT NULL COMMENT '球队唯一编码',
  `team_name` varchar(50) NOT NULL COMMENT '球队全称',
  `team_short_name` varchar(20) NOT NULL COMMENT '球队简称',
  `city` varchar(50) DEFAULT NULL COMMENT '所在城市',
  `conference` varchar(20) DEFAULT NULL COMMENT '所属联盟：东部/西部',
  `division` varchar(20) DEFAULT NULL COMMENT '所属分区',
  `logo_url` varchar(255) DEFAULT NULL COMMENT '球队logo地址',
  `found_year` varchar(10) DEFAULT NULL COMMENT '成立年份',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_code` (`team_code`,`is_deleted`) COMMENT '球队编码唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='NBA球队表';

-- 3. 球员基础信息表
CREATE TABLE `t_player` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `player_id` bigint NOT NULL COMMENT 'NBA官方球员唯一ID',
  `chinese_name` varchar(50) NOT NULL COMMENT '中文姓名',
  `english_name` varchar(100) NOT NULL COMMENT '英文姓名',
  `country` varchar(50) DEFAULT NULL COMMENT '国籍',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `height` decimal(5,2) DEFAULT NULL COMMENT '身高(米)',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重(公斤)',
  `position` varchar(30) DEFAULT NULL COMMENT '场上位置',
  `jersey_number` varchar(10) DEFAULT NULL COMMENT '球衣号码',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-退役，1-现役',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_player_id` (`player_id`,`is_deleted`) COMMENT '官方球员ID唯一索引',
  KEY `idx_chinese_name` (`chinese_name`) COMMENT '中文姓名查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='NBA球员基础信息表';

-- 4. 球员赛季数据核心表
CREATE TABLE `t_player_season_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `season_id` bigint NOT NULL COMMENT '赛季ID，关联t_season.id',
  `player_id` bigint NOT NULL COMMENT '球员ID，关联t_player.player_id',
  `team_id` bigint NOT NULL COMMENT '球队ID，关联t_team.id',
  `rank` int DEFAULT NULL COMMENT '得分排名',
  `games` int NOT NULL DEFAULT '0' COMMENT '比赛场次',
  `minutes_per_game` decimal(4,1) DEFAULT NULL COMMENT '场均出场时间(分钟)',
  `points_per_game` decimal(4,1) NOT NULL DEFAULT '0.0' COMMENT '场均得分',
  `rebounds_per_game` decimal(4,1) NOT NULL DEFAULT '0.0' COMMENT '场均篮板',
  `assists_per_game` decimal(4,1) NOT NULL DEFAULT '0.0' COMMENT '场均助攻',
  `steals_per_game` decimal(3,1) NOT NULL DEFAULT '0.0' COMMENT '场均抢断',
  `blocks_per_game` decimal(3,1) NOT NULL DEFAULT '0.0' COMMENT '场均盖帽',
  `field_goal_percentage` decimal(4,1) DEFAULT NULL COMMENT '投篮命中率(%)',
  `three_pointers_made` int NOT NULL DEFAULT '0' COMMENT '三分命中总数',
  `three_point_percentage` decimal(4,1) DEFAULT NULL COMMENT '三分命中率(%)',
  `free_throw_percentage` decimal(4,1) DEFAULT NULL COMMENT '罚球命中率(%)',
  `data_update_time` datetime DEFAULT NULL COMMENT '官方数据更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_season_player_team` (`season_id`,`player_id`,`team_id`,`is_deleted`) COMMENT '赛季+球员+球队唯一索引',
  KEY `idx_season_id` (`season_id`) COMMENT '赛季查询索引',
  KEY `idx_team_id` (`team_id`) COMMENT '球队查询索引',
  KEY `idx_points_rank` (`season_id`,`rank`) COMMENT '得分排名排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='NBA球员赛季数据统计表';

-- 5. 系统用户表
CREATE TABLE `t_sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`,`is_deleted`) COMMENT '账号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 6. 系统角色表
CREATE TABLE `t_sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`,`is_deleted`) COMMENT '角色编码唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 7. 系统权限表
CREATE TABLE `t_sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父权限ID',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) NOT NULL COMMENT '权限标识',
  `permission_type` tinyint NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径/接口路径',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`,`is_deleted`) COMMENT '权限标识唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 8. 用户角色关联表
CREATE TABLE `t_sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) COMMENT '用户角色唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 9. 角色权限关联表
CREATE TABLE `t_sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`) COMMENT '角色权限唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 10. 系统操作日志表
CREATE TABLE `t_sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operation_user` varchar(50) NOT NULL COMMENT '操作人账号',
  `operation_type` varchar(30) NOT NULL COMMENT '操作类型：新增/编辑/删除/查询/同步',
  `operation_module` varchar(50) NOT NULL COMMENT '操作模块',
  `operation_content` varchar(500) DEFAULT NULL COMMENT '操作内容',
  `request_ip` varchar(50) DEFAULT NULL COMMENT '请求IP',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求接口',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方式',
  `status` tinyint NOT NULL COMMENT '操作状态：0-失败，1-成功',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '失败信息',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operation_user` (`operation_user`) COMMENT '操作人查询索引',
  KEY `idx_operation_time` (`operation_time`) COMMENT '操作时间排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';
```

### 5.4 索引设计规范

1. **主键索引**：所有表必须设置自增主键 ID，保证数据唯一性与查询性能
2. **唯一索引**：业务唯一键必须建立唯一索引，防止重复数据，如赛季 + 球员 + 球队的联合唯一索引
3. **普通索引**：高频查询字段、关联字段、排序字段建立普通索引，如赛季 ID、球队 ID、球员姓名、得分排名
4. **联合索引**：多条件联合查询场景建立联合索引，遵循最左匹配原则，如赛季 ID + 排名的联合索引
5. **索引优化**：单表索引数量不超过 8 个，避免宽索引，索引字段尽量设置为 NOT NULL，减少索引空间占用

## 6. 安全策略配置

### 6.1 身份认证与权限控制

1. 身份认证体系

   - 后台管理采用 JWT+Spring Security 实现无状态身份认证，Token 有效期 2 小时，支持刷新 Token 机制
   - 密码采用 BCrypt 不可逆加密算法存储，禁止明文存储密码，强制密码复杂度要求（8 位以上，包含大小写、数字、特殊字符）
   - 登录失败次数限制：连续 5 次登录失败锁定账号 30 分钟，防止暴力破解

   

2. RBAC 权限控制模型

   - 采用「用户 - 角色 - 权限」三级权限模型，实现细粒度的权限管控
   - 权限分为菜单权限、按钮权限、接口权限三个维度，接口层通过注解实现权限拦截，无权限请求直接拒绝
   - 角色分为超级管理员、数据管理员、普通查看员，默认角色权限不可删除，支持自定义角色

   

3. 会话安全

   - 后台管理系统设置会话超时时间，无操作 30 分钟自动退出登录
   - 同一账号同时在线设备数限制，最多支持 2 台设备同时登录
   - Token 绑定用户 IP 与设备信息，异常环境自动失效

   

### 6.2 接口安全防护

1. 接口限流与熔断

   - 基于 Sentinel 实现接口限流，公开接口单 IP 限流：100 次 / 分钟，防止恶意刷接口
   - 核心查询接口设置熔断降级策略，服务异常时返回缓存兜底数据，保障服务可用性
   - 爬虫防护：通过 User-Agent 校验、Cookie 校验、高频 IP 封禁，防止恶意爬取数据

   

2. 接口幂等性保障

   - 写操作接口（新增、编辑、删除、同步）通过请求唯一 ID 实现幂等性控制，防止重复提交
   - 核心数据修改接口采用乐观锁机制，防止并发修改导致的数据不一致

   

3. 输入参数校验

   - 所有接口入参必须进行合法性校验，包括参数类型、长度、范围、格式校验，非法参数直接拦截
   - 路径参数、查询参数、请求体参数全量校验，防止参数溢出与非法注入

   

### 6.3 数据安全保障

1. 数据脱敏

   - 后台用户敏感信息（手机号、邮箱）在前端展示时进行脱敏处理，仅管理员可查看完整信息
   - 接口返回数据过滤敏感字段，密码、加密盐等信息禁止在任何接口中返回

   

2. 数据备份与容灾

   - 数据库采用主从架构，主库写入，从库读取，保障数据高可用
   - 全量数据每日凌晨备份，增量数据每小时备份，备份文件异地存储，保留 30 天备份历史
   - 核心表数据修改前自动备份快照，支持数据误操作回滚

   

3. 数据加密

   - 传输层全站启用 HTTPS 协议，禁止 HTTP 明文传输，防止数据传输过程中被窃听、篡改
   - 敏感配置信息（数据库密码、API 密钥）采用 AES 加密存储，禁止明文写在配置文件中

   

### 6.4 Web 应用安全防护

1. SQL 注入防护

   - 所有数据库操作采用 MyBatis-Plus 参数绑定机制，禁止拼接 SQL 语句
   - 输入参数进行特殊字符过滤与转义，拦截 SQL 注入关键词
   - 数据库账号最小权限原则，业务账号仅授予必要的增删改查权限，禁止 drop、alter 等高风险权限

   

2. XSS 跨站脚本防护

   - 前端输入内容进行标签与特殊字符转义，禁止恶意脚本执行
   - 后端添加 XSS 过滤器，对所有请求参数进行恶意脚本检测与过滤
   - 响应头设置 Content-Security-Policy（CSP）策略，限制外部资源加载与内联脚本执行

   

3. CSRF 跨站请求伪造防护

   - 所有写操作请求必须携带 CSRF Token，服务端校验 Token 有效性
   - 接口校验请求来源 Referer，拦截非法跨域请求
   - 跨域配置严格限制允许的域名，禁止全量开放跨域权限

   

4. 其他安全防护

   - 响应头添加 X-Content-Type-Options、X-Frame-Options、X-XSS-Protection 等安全头，防止点击劫持、MIME 类型嗅探
   - 禁用 TRACE、PUT、DELETE 等非必要 HTTP 方法，关闭 Web 容器目录浏览功能
   - 定期进行漏洞扫描与代码安全审计，及时修复安全漏洞

   

### 6.5 日志与审计

1. 全量操作日志

   - 后台所有写操作、敏感查询操作全量记录操作日志，包含操作人、操作时间、操作内容、请求 IP、操作状态等信息
   - 操作日志永久存储，不可修改、不可删除，用于安全审计与问题追溯

   

2. 异常日志监控

   - 系统异常、接口错误、权限拒绝等事件全量记录异常日志，实时推送告警
   - 定期分析异常日志，识别潜在的安全攻击与系统风险

   

3. 访问日志记录

   - 网关层记录所有接口的访问日志，包含请求 IP、请求路径、请求参数、响应状态、耗时等信息
   - 访问日志保留 90 天，支持异常访问溯源与攻击行为分析

   

## 7. 部署与运维架构

### 7.1 部署架构

采用 Kubernetes 容器化集群部署，整体架构如下：

1. **环境划分**：分为开发环境、测试环境、生产环境，环境之间网络隔离，配置独立

2. 生产环境部署架构

   - 前端静态资源：打包后上传至阿里云 OSS，通过 CDN 加速分发
   - 网关层：多副本部署 Nginx 网关与 Spring Cloud Gateway，实现负载均衡与故障转移
   - 应用服务层：核心服务多副本部署，根据流量自动弹性扩缩容
   - 数据库层：MySQL 一主两从架构，主库负责写操作，从库负责读操作，开启半同步复制
   - 缓存层：Redis 三主三从集群架构，开启持久化，保障缓存数据高可用
   - 中间件层：Elasticsearch、XXL-Job、Kafka 等中间件均采用集群部署，避免单点故障

   

3. **CI/CD 流水线**：基于 Jenkins 实现自动化构建、测试、部署，代码提交后自动触发流水线，实现分钟级发布

### 7.2 监控告警体系

1. 全维度监控

   - 基础设施监控：服务器 CPU、内存、磁盘、网络使用率监控
   - 容器监控：Pod 状态、资源使用率、重启次数监控
   - 应用监控：服务接口响应时间、吞吐量、错误率、JVM 状态监控
   - 数据库监控：MySQL 连接数、慢查询、锁等待、主从同步状态监控
   - 业务监控：数据同步成功率、接口调用量、用户访问量监控

   

2. 告警机制

   - 多级别告警：分为紧急、重要、警告、提示四个级别，不同级别对应不同的通知渠道
   - 通知渠道：支持短信、企业微信、邮件告警，紧急告警 15 分钟内未处理自动升级通知
   - 告警降噪：相同告警合并处理，设置告警抑制规则，避免告警风暴

   

### 7.3 数据备份与容灾

1. 备份策略

   - 数据库：每日全量备份，每小时增量备份，备份文件同时存储在本地与异地 OSS
   - 配置文件：所有配置文件纳入 Git 版本管理，同时备份至配置中心
   - 业务数据：核心业务表每日快照备份，保留 30 天历史快照

   

2. 容灾恢复

   - 制定完整的灾难恢复预案，定期进行容灾演练，保障 RTO<30 分钟，RPO<1 小时
   - 支持单节点故障自动转移，集群故障快速切换至备用集群
   - 备份文件定期进行恢复测试，保障备份文件可用性

   

## 8. 系统扩展性设计

### 8.1 水平扩展能力

1. **服务无状态化**：所有业务服务均设计为无状态服务，可通过增加副本数实现水平扩展，应对流量增长
2. **读写分离**：数据库读写分离，读请求可通过增加从库节点扩展读性能，写请求通过分库分表实现扩展
3. **缓存集群**：Redis 集群支持动态扩容，应对热点数据缓存压力增长

### 8.2 业务功能扩展

1. **模块化设计**：系统采用模块化、插件化设计，新增业务模块无需修改现有代码，遵循开闭原则
2. **接口标准化**：所有接口遵循统一的 RESTful 规范，新增接口复用统一的鉴权、限流、日志、异常处理体系
3. **配置化管理**：业务规则、筛选维度、展示字段均支持配置化管理，无需修改代码即可调整业务逻辑

### 8.3 数据维度扩展

1. **数据模型兼容**：球员数据模型预留扩展字段，新增统计指标无需修改表结构，兼容未来数据维度扩展
2. **多赛事类型扩展**：架构支持扩展季前赛、全明星赛、总决赛等赛事类型的数据统计，无需重构核心架构
3. **历史数据兼容**：支持历史赛季数据的批量导入与归档，兼容不同赛季的数据格式差异