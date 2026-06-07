# 系统管理员模块 - UML图

## 一、用例图

系统管理员负责系统的日常运维、数据维护和用户管理工作。

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei
skinparam actorStyle awesome

actor "系统管理员" as Admin

rectangle "NBA数据分析系统" {
    usecase "用户管理" as UC1
    usecase "数据维护" as UC2
    usecase "系统配置" as UC3
    usecase "日志查看" as UC4
    usecase "数据更新" as UC5
    usecase "API密钥管理" as UC6
    usecase "查看用户列表" as UC1_1
    usecase "禁用/启用用户" as UC1_2
    usecase "重置用户密码" as UC1_3
    usecase "维护球队数据" as UC2_1
    usecase "维护球员数据" as UC2_2
}

Admin --> UC1
Admin --> UC2
Admin --> UC3
Admin --> UC4
Admin --> UC5
UC1 ..> UC1_1 : <<include>>
UC1 ..> UC1_2 : <<include>>
UC1 ..> UC1_3 : <<include>>
UC2 ..> UC2_1 : <<include>>
UC2 ..> UC2_2 : <<include>>
UC3 ..> UC6 : <<include>>

@enduml
```

### 用例说明

| 用例 | 说明 |
|------|------|
| 用户管理 | 管理系统用户账号，包括查看、禁用、重置密码等 |
| 数据维护 | 维护球队、球员等基础数据的完整性 |
| 系统配置 | 配置系统参数和外部服务密钥 |
| 日志查看 | 查看系统运行日志和错误记录 |
| 数据更新 | 更新NBA比赛数据和球员统计数据 |
| API密钥管理 | 管理智谱AI、SearchAPI等外部服务密钥 |

---

## 二、活动图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

start
:管理员登录系统;

if (选择功能模块?) then (用户管理)
    :查看用户列表;
    :搜索用户;
    if (操作类型?) then (禁用用户)
        :选择用户;
        :禁用账号;
        :保存修改;
    else (重置密码)
        :选择用户;
        :生成新密码;
        :发送通知;
    endif
else (数据维护)
    if (数据类型?) then (球队数据)
        :查看球队列表;
        :编辑球队信息;
        :保存修改;
    else (球员数据)
        :查看球员列表;
        :编辑球员信息;
        :保存修改;
    endif
endif

if (继续操作?) then (是)
    :返回功能选择;
else (否)
endif

if (系统配置?) then (是)
    if (配置类型?) then (API密钥)
        :配置智谱AI密钥;
        :配置SearchAPI密钥;
        :保存配置;
    else (系统参数)
        :配置超时时间;
        :配置默认模型;
        :保存配置;
    endif
else (否)
endif

if (查看日志?) then (是)
    :选择日志类型;
    :设置时间范围;
    :查看日志详情;
    if (发现异常?) then (是)
        :记录问题;
        :处理异常;
    else (否)
    endif
else (否)
endif

:退出系统;
stop
@enduml
```

---

## 三、类图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei
skinparam classAttributeIconSize 0

class AdminController {
    -userService: UserService
    -dataService: DataService
    -configService: ConfigService
    -logService: LogService
    +getUserList(): Result
    +disableUser(): Result
    +resetPassword(): Result
    +updateTeamData(): Result
    +updatePlayerData(): Result
    +getSystemConfig(): Result
    +updateApiKeys(): Result
    +getLogs(): Result
}

class UserService {
    -userMapper: UserMapper
    +findAll(): List<User>
    +findById(): User
    +disableUser(): boolean
    +resetPassword(): String
    +updateStatus(): boolean
}

class DataService {
    -teamMapper: TeamMapper
    -playerMapper: PlayerMapper
    +updateTeam(): boolean
    +updatePlayer(): boolean
    +batchUpdate(): boolean
    +validateData(): boolean
}

class ConfigService {
    -configMapper: ConfigMapper
    +getApiKeys(): Config
    +updateApiKey(): boolean
    +getSystemParams(): Config
    +updateSystemParams(): boolean
}

class LogService {
    -logMapper: LogMapper
    +getLogs(): List<Log>
    +getLogById(): Log
    +addErrorLog(): void
    +clearOldLogs(): void
}

class User {
    -id: Integer
    -username: String
    -password: String
    -status: Integer
    -createTime: LocalDateTime
}

class Config {
    -id: Integer
    -configKey: String
    -configValue: String
    -description: String
    -updateTime: LocalDateTime
}

class Log {
    -id: Integer
    -logType: String
    -logContent: String
    -createTime: LocalDateTime
    -userId: Integer
}

class UserMapper {
    +findAll(): List<User>
    +findById(): User
    +updateStatus(): int
    +updatePassword(): int
}

class TeamMapper {
    +findAll(): List<Team>
    +update(): int
}

class PlayerMapper {
    +findAll(): List<Player>
    +update(): int
}

class ConfigMapper {
    +findByKey(): Config
    +update(): int
}

class LogMapper {
    +findByCondition(): List<Log>
    +insert(): int
    +deleteOld(): int
}

AdminController --> UserService
AdminController --> DataService
AdminController --> ConfigService
AdminController --> LogService
UserService --> UserMapper
UserService --> User
DataService --> TeamMapper
DataService --> PlayerMapper
ConfigService --> ConfigMapper
ConfigService --> Config
LogService --> LogMapper
LogService --> Log
UserMapper --> User
ConfigMapper --> Config
LogMapper --> Log

@enduml
```

### 类职责说明

| 类名 | 类型 | 职责 |
|------|------|------|
| AdminController | 控制器 | 处理管理员相关请求，提供管理API接口 |
| UserService | 服务层 | 用户管理业务逻辑 |
| DataService | 服务层 | 数据维护业务逻辑 |
| ConfigService | 服务层 | 系统配置业务逻辑 |
| LogService | 服务层 | 日志管理业务逻辑 |
| User | 实体类 | 用户信息实体 |
| Config | 实体类 | 系统配置实体 |
| Log | 实体类 | 系统日志实体 |

---

## 四、时序图

### 4.1 用户管理时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 管理员 as Admin
participant "管理后台" as Frontend
participant "AdminController" as Controller
participant "UserService" as Service
participant "UserMapper" as Mapper
database "MySQL数据库" as DB

Admin -> Frontend : 登录管理后台
activate Frontend

Frontend -> Controller : GET /api/admin/users
activate Controller

Controller -> Service : findAll()
activate Service

Service -> Mapper : findAll()
activate Mapper

Mapper -> DB : SELECT * FROM emp
activate DB

DB --> Mapper : 返回用户列表
deactivate DB

Mapper --> Service : 返回List<User>
deactivate Mapper

Service --> Controller : 返回用户列表
deactivate Service

Controller --> Frontend : Result.success(users)
deactivate Controller

Frontend --> Admin : 显示用户列表

Admin -> Frontend : 选择禁用用户
Frontend -> Controller : POST /api/admin/user/disable
activate Controller

Controller -> Service : disableUser(userId)
activate Service

Service -> Mapper : updateStatus(userId, 0)
activate Mapper

Mapper -> DB : UPDATE emp SET status = 0 WHERE id = ?
activate DB

DB --> Mapper : 更新成功
deactivate DB

Mapper --> Service : 返回影响行数
deactivate Mapper

Service --> Controller : 返回结果
deactivate Service

Controller --> Frontend : Result.success("禁用成功")
deactivate Controller

Frontend --> Admin : 提示禁用成功

deactivate Frontend

@enduml
```

### 4.2 数据维护时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 管理员 as Admin
participant "管理后台" as Frontend
participant "AdminController" as Controller
participant "DataService" as Service
participant "TeamMapper" as TeamMapper
participant "PlayerMapper" as PlayerMapper
database "MySQL数据库" as DB

Admin -> Frontend : 选择数据维护
activate Frontend

Frontend -> Controller : GET /api/admin/teams
activate Controller

Controller -> Service : getAllTeams()
activate Service

Service -> TeamMapper : findAll()
activate TeamMapper

TeamMapper -> DB : SELECT * FROM team
activate DB

DB --> TeamMapper : 返回球队列表
deactivate DB

TeamMapper --> Service : 返回List<Team>
deactivate TeamMapper

Service --> Controller : 返回球队列表
deactivate Service

Controller --> Frontend : Result.success(teams)
deactivate Controller

Frontend --> Admin : 显示球队列表

Admin -> Frontend : 编辑球队信息
Frontend -> Controller : POST /api/admin/team/update
activate Controller

Controller -> Service : updateTeam(team)
activate Service

Service -> Service : validateData()
note right: 验证数据完整性

Service -> TeamMapper : update(team)
activate TeamMapper

TeamMapper -> DB : UPDATE team SET ... WHERE teamid = ?
activate DB

DB --> TeamMapper : 更新成功
deactivate DB

TeamMapper --> Service : 返回影响行数
deactivate TeamMapper

Service --> Controller : 返回结果
deactivate Service

Controller --> Frontend : Result.success("更新成功")
deactivate Controller

Frontend --> Admin : 提示更新成功

deactivate Frontend

@enduml
```

### 4.3 系统配置时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 管理员 as Admin
participant "管理后台" as Frontend
participant "AdminController" as Controller
participant "ConfigService" as Service
participant "ConfigMapper" as Mapper
database "MySQL数据库" as DB

Admin -> Frontend : 进入系统配置
activate Frontend

Frontend -> Controller : GET /api/admin/config
activate Controller

Controller -> Service : getApiKeys()
activate Service

Service -> Mapper : findByKey("ai.api.key")
activate Mapper

Mapper -> DB : SELECT * FROM config WHERE config_key = ?
activate DB

DB --> Mapper : 返回配置
deactivate DB

Mapper --> Service : 返回Config
deactivate Mapper

Service --> Controller : 返回配置信息
deactivate Service

Controller --> Frontend : Result.success(config)
deactivate Controller

Frontend --> Admin : 显示当前配置

Admin -> Frontend : 修改API密钥
Frontend -> Controller : POST /api/admin/config/apikey
activate Controller

Controller -> Service : updateApiKey(key, value)
activate Service

Service -> Mapper : update(config)
activate Mapper

Mapper -> DB : UPDATE config SET config_value = ? WHERE config_key = ?
activate DB

DB --> Mapper : 更新成功
deactivate DB

Mapper --> Service : 返回影响行数
deactivate Mapper

Service --> Controller : 返回结果
deactivate Service

Controller --> Frontend : Result.success("配置更新成功")
deactivate Controller

Frontend --> Admin : 提示配置更新成功

deactivate Frontend

@enduml
```

### 4.4 日志查看时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 管理员 as Admin
participant "管理后台" as Frontend
participant "AdminController" as Controller
participant "LogService" as Service
participant "LogMapper" as Mapper
database "MySQL数据库" as DB

Admin -> Frontend : 进入日志管理
activate Frontend

Frontend -> Controller : GET /api/admin/logs
activate Controller
note right: 参数: type, startDate, endDate

Controller -> Service : getLogs(condition)
activate Service

Service -> Mapper : findByCondition(type, startDate, endDate)
activate Mapper

Mapper -> DB : SELECT * FROM log WHERE ... ORDER BY create_time DESC
activate DB

DB --> Mapper : 返回日志列表
deactivate DB

Mapper --> Service : 返回List<Log>
deactivate Mapper

Service --> Controller : 返回日志列表
deactivate Service

Controller --> Frontend : Result.success(logs)
deactivate Controller

Frontend --> Admin : 显示日志列表

Admin -> Frontend : 查看日志详情
Frontend -> Controller : GET /api/admin/log/{id}
activate Controller

Controller -> Service : getLogById(id)
activate Service

Service -> Mapper : findById(id)
activate Mapper

Mapper -> DB : SELECT * FROM log WHERE id = ?
activate DB

DB --> Mapper : 返回日志详情
deactivate DB

Mapper --> Service : 返回Log
deactivate Mapper

Service --> Controller : 返回日志详情
deactivate Service

Controller --> Frontend : Result.success(log)
deactivate Controller

Frontend --> Admin : 显示日志详情

deactivate Frontend

@enduml
```

---

## 五、管理员功能列表

| 功能模块 | 功能项 | 操作 |
|----------|--------|------|
| 用户管理 | 用户列表 | 查看、搜索、筛选 |
| 用户管理 | 账号状态 | 禁用、启用 |
| 用户管理 | 密码管理 | 重置密码 |
| 数据维护 | 球队数据 | 查看、编辑、更新 |
| 数据维护 | 球员数据 | 查看、编辑、更新 |
| 系统配置 | API密钥 | 配置智谱AI、SearchAPI密钥 |
| 系统配置 | 系统参数 | 配置超时时间、默认模型 |
| 日志管理 | 日志查看 | 按类型、时间查看日志 |
| 日志管理 | 日志清理 | 清理过期日志 |

---

## 六、管理员API接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/admin/users | GET | 获取用户列表 |
| /api/admin/user/disable | POST | 禁用用户 |
| /api/admin/user/enable | POST | 启用用户 |
| /api/admin/user/reset-password | POST | 重置用户密码 |
| /api/admin/teams | GET | 获取球队列表 |
| /api/admin/team/update | POST | 更新球队数据 |
| /api/admin/players | GET | 获取球员列表 |
| /api/admin/player/update | POST | 更新球员数据 |
| /api/admin/config | GET | 获取系统配置 |
| /api/admin/config/apikey | POST | 更新API密钥 |
| /api/admin/logs | GET | 获取日志列表 |
| /api/admin/log/{id} | GET | 获取日志详情 |
