# StatCourt · NBA数据球场

基于NBA官方数据的全维度球员数据统计与分析平台，集成AI智能分析、比赛预测、3D可视化等核心功能。

## 项目概述

StatCourt是面向NBA球迷、数据分析师和体育媒体的综合数据服务平台。平台聚焦NBA 2024-2025赛季球员数据，提供从基础数据查询到高阶统计分析、从多维度对比到AI智能解读的全链路服务，帮助用户深度洞察NBA赛场数据。

## 技术栈

| 层级 | 技术选型 |
|------|---------|
| 前端 | HTML5 + CSS3 + JavaScript (ES6+) |
| 可视化 | ECharts + Three.js + Leaflet + GSAP |
| 后端 | Spring Boot 3.2 + MyBatis 3.0 + Maven |
| 数据库 | MySQL 8.0 / SQLite |
| 数据采集 | Python + Requests + Pandas |
| AI | 智谱AI GLM API + SearchAPI |

## 项目结构

```
StatCourt/
├── site/                              # 前端静态网站
│   ├── index.html                     # 首页 — 数据榜单、热门球员、球队荣誉墙
│   ├── data.html                      # 数据页 — 球员/球队数据查询
│   ├── advanced.html                  # 高阶数据页 — PER、WS、BPM等专业指标
│   ├── visualization.html             # 可视化页 — ECharts 多维度图表
│   ├── three3d.html                   # 3D可视化页 — Three.js 三维数据展示
│   ├── map.html                       # 球队地图页 — Leaflet 地理分布
│   ├── compare.html                   # 对比页 — 球员数据多维度对比
│   ├── chat.html                      # AI助手 — 智能对话、联网搜索、图片分析
│   ├── predict.html                   # 比赛预测 — AI赛果预测与分析报告
│   ├── teams.html                     # 球队列表页
│   ├── profile.html                   # 个人中心 — 资料管理、收藏、历史
│   ├── admin.html                     # 后台管理 — 用户管理、数据管理
│   ├── login.html                     # 登录/注册
│   ├── css/
│   │   ├── styles.css                 # 全局样式
│   │   ├── index.css                  # 首页样式
│   │   ├── data.css                   # 数据页样式
│   │   ├── weblogin.css               # 登录样式
│   │   └── teams.css                  # 球队页样式
│   ├── js/
│   │   ├── nav.js                     # 导航栏（统一加载）
│   │   ├── main.js                    # 主脚本
│   │   └── gsap-animations.js         # GSAP 交互动画
│   └── assets/                        # 静态资源（球队Logo、数据文件等）
│
├── backend-demo/                      # Spring Boot 后端服务
│   ├── pom.xml
│   └── src/main/java/com/nba/demo/
│       ├── controller/                # 控制器层
│       │   ├── DataController.java    # 球员/球队数据查询
│       │   ├── AdvancedStatsController.java  # 高阶数据
│       │   ├── AuthController.java    # 用户认证
│       │   ├── AdminController.java   # 后台管理
│       │   ├── AIController.java      # AI对话
│       │   ├── PredictionController.java    # 比赛预测
│       │   ├── UserLineupController.java    # 用户阵容管理
│       │   └── UserTeamController.java      # 用户球队管理
│       ├── service/                   # 业务逻辑层
│       ├── entity/                    # 实体类
│       ├── mapper/                    # MyBatis 数据访问
│       ├── config/                    # 配置类（CORS等）
│       └── common/                    # 公共工具
│
├── scripts/                           # Python 数据与预测脚本
│   ├── config.py                      # 配置管理
│   ├── data_collection.py             # NBA数据采集
│   ├── feature_engineering.py         # 特征工程
│   ├── match_predictor.py             # 比赛预测模型
│   └── requirements.txt
│
├── ml-service/                        # 机器学习服务（Python Flask）
├── database_setup.py                  # 数据库初始化脚本
├── 系统功能说明.md                     # 详细功能文档
├── 系统整体架构.md                     # 架构设计文档
├── 系统活动图.md / 系统管理员UML图.md    # 设计图纸
├── AI业务逻辑说明.md                   # AI模块设计
└── AI对话模块设计文档.md               # AI对话架构
```

## 核心功能

### 1. 数据查询
- **球员数据**：按球队筛选、关键词搜索、多维度排序（得分/篮板/助攻等）
- **球队数据**：全部球队排名、球队详情、核心球员信息
- **高阶数据**：PER效率值、WS胜利贡献值、BPM正负值、VORP替代价值等专业指标

### 2. 数据对比
- 支持两名球员多维度能力对比（雷达图 + 柱状图）
- 基础数据与高阶数据两种对比模式
- 历史对比记录保存与管理

### 3. 数据可视化
- **2D图表**：ECharts 雷达图、柱状图、折线图、散点图
- **3D可视化**：Three.js 三维立体展示，鼠标拖拽旋转/缩放
- **球队地图**：Leaflet 30支球队地理分布，东西部联盟筛选

### 4. AI智能分析
- **AI对话**：集成智谱AI大模型，NBA知识问答
- **联网搜索**：获取最新NBA资讯
- **图片分析**：上传球员/比赛图片进行智能识别

### 5. 比赛预测
- 基于球队战绩和场均数据的胜负预测
- AI生成详细分析报告（关键因素、优劣势对比等）

### 6. 用户系统
- 注册/登录（Token认证）
- 个人中心（资料管理、收藏、历史记录）
- 阵容管理与球队管理
- 后台管理（用户管理、数据管理等）

### 7. Python数据脚本
- `data_collection.py`：自动采集NBA官方数据
- `feature_engineering.py`：构建预测特征
- `match_predictor.py`：基于ML的比赛预测

## 快速开始

### 前端

```bash
cd site

# 方式一：直接用浏览器打开 index.html
# 方式二：使用 Python 启动本地服务器
python -m http.server 8000
```

访问 http://localhost:8000

### 后端

```bash
cd backend-demo
# 配置 application.yml 中的数据库连接
mvn clean install
mvn spring-boot:run
```

后端服务地址：http://localhost:8080

### Python 数据脚本

```bash
cd scripts
pip install -r requirements.txt
python data_collection.py    # 采集NBA数据
python match_predictor.py    # 运行比赛预测
```

## 接口概览

| 接口 | 说明 |
|------|------|
| GET /api/players | 球员数据列表（支持筛选/排序/搜索） |
| GET /api/player/{id} | 球员详情 |
| GET /api/teams | 球队列表 |
| GET /api/team/{id} | 球队详情 |
| GET /api/advanced-stats | 高阶数据 |
| GET /api/compare | 球员对比数据 |
| POST /api/auth/login | 用户登录 |
| POST /api/auth/register | 用户注册 |
| POST /api/ai/chat | AI对话 |
| POST /api/predict | 比赛预测 |

## 数据库

支持 MySQL 8.0 和 SQLite 双模式运行。

```bash
# MySQL 初始化
mysql -u root -p < database_setup.py

# SQLite（开发模式，开箱即用）
# 默认使用项目根目录的 nba_stats.db
```

## 版权信息

Copyright © 2026 StatCourt · NBA数据球场
