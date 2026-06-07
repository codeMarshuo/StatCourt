# 用户管理模块 - UML图

## 一、活动图

用户管理模块的活动图展示了用户注册、登录、信息管理等操作的完整流程。

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

start
:访问系统;

if (已登录?) then (是)
    :直接进入系统;
else (否)
    if (选择注册?) then (是)
        :填写用户名;
        :填写密码;
        :验证用户名唯一性;
        if (验证通过?) then (是)
            :创建用户账号;
            :初始化用户统计;
            :注册成功;
        else (否)
            :提示用户名已存在;
        endif
    else (否)
        :输入用户名密码;
        :查询用户信息;
        if (验证通过?) then (是)
            :生成Token;
            :更新登录时间;
            :登录成功;
        else (否)
            :提示用户名或密码错误;
        endif
    endif
endif

:进入用户中心;

if (修改个人信息?) then (是)
    :修改姓名;
    :修改头像;
    :保存修改;
else (否)
endif

if (修改密码?) then (是)
    :输入原密码;
    :输入新密码;
    if (原密码正确?) then (是)
        :更新密码;
        :密码修改成功;
    else (否)
        :提示原密码错误;
    endif
else (否)
endif

if (设置喜好?) then (是)
    :选择喜好球队;
    :选择喜好球员;
    :保存喜好设置;
else (否)
endif

if (退出登录?) then (是)
    :清除Token;
    :退出成功;
else (否)
endif

stop
@enduml
```

---

## 二、类图

用户管理模块的类图展示了各实体类、控制器和数据访问层之间的关系。

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei
skinparam classAttributeIconSize 0

class AuthController {
    -empMapper: EmpMapper
    -userStatsMapper: UserStatsMapper
    +login(): Result
    +register(): Result
    +logout(): Result
    +getUserInfo(): Result
    +updateUserInfo(): Result
    +updatePassword(): Result
    +updateFavorites(): Result
    +getFavorites(): Result
    +incrementStats(): Result
}

class Emp {
    -id: Integer
    -username: String
    -password: String
    -name: String
    -gender: Integer
    -phone: String
    -image: String
    -createTime: LocalDateTime
    -updateTime: LocalDateTime
}

class UserStats {
    -id: Integer
    -username: String
    -viewCount: Integer
    -favoriteCount: Integer
    -predictionCount: Integer
    -lastLoginDate: LocalDate
    -favoriteTeam: String
    -favoritePlayer: String
}

class EmpMapper {
    +findByUsername(): Emp
    +findById(): Emp
    +insert(): int
    +update(): int
    +updatePassword(): int
    +countByUsername(): int
}

class UserStatsMapper {
    +findByUsername(): UserStats
    +insert(): int
    +updateFavorites(): int
    +updateLastLoginDate(): int
    +incrementViewCount(): int
    +incrementFavoriteCount(): int
    +incrementPredictionCount(): int
}

class Result {
    -code: Integer
    -message: String
    -data: Object
    +success(): Result
    +error(): Result
}

AuthController --> EmpMapper
AuthController --> UserStatsMapper
AuthController --> Result
EmpMapper --> Emp
UserStatsMapper --> UserStats

@enduml
```

---

## 三、时序图

### 3.1 用户登录时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 用户 as User
participant "前端页面" as Frontend
participant "AuthController" as Controller
participant "EmpMapper" as EmpMapper
participant "UserStatsMapper" as StatsMapper
database "MySQL数据库" as DB

User -> Frontend : 输入用户名密码
activate Frontend

Frontend -> Controller : POST /api/login
activate Controller

Controller -> EmpMapper : findByUsername(username)
activate EmpMapper

EmpMapper -> DB : SELECT * FROM emp WHERE username = ?
activate DB

DB --> EmpMapper : 返回用户信息
deactivate DB

EmpMapper --> Controller : 返回Emp对象
deactivate EmpMapper

alt 用户不存在
    Controller --> Frontend : Result.error("用户不存在")
    Frontend --> User : 提示用户不存在
else 密码错误
    Controller --> Frontend : Result.error("密码错误")
    Frontend --> User : 提示密码错误
else 登录成功
    Controller -> Controller : 生成Token
    Controller -> StatsMapper : updateLastLoginDate()
    activate StatsMapper
    
    StatsMapper -> DB : UPDATE user_stats SET last_login_date = ?
    activate DB
    DB --> StatsMapper : 更新成功
    deactivate DB
    
    StatsMapper --> Controller : 返回结果
    deactivate StatsMapper
    
    Controller --> Frontend : Result.success(token, userInfo)
    deactivate Controller
    
    Frontend -> Frontend : 保存Token到本地存储
    Frontend --> User : 跳转到首页
end

deactivate Frontend

@enduml
```

### 3.2 用户注册时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 用户 as User
participant "前端页面" as Frontend
participant "AuthController" as Controller
participant "EmpMapper" as EmpMapper
participant "UserStatsMapper" as StatsMapper
database "MySQL数据库" as DB

User -> Frontend : 填写注册信息
activate Frontend

Frontend -> Frontend : 验证表单数据

Frontend -> Controller : POST /api/register
activate Controller

Controller -> EmpMapper : countByUsername(username)
activate EmpMapper

EmpMapper -> DB : SELECT COUNT(*) FROM emp WHERE username = ?
activate DB

DB --> EmpMapper : 返回数量
deactivate DB

EmpMapper --> Controller : 返回用户数量
deactivate EmpMapper

alt 用户名已存在
    Controller --> Frontend : Result.error("用户名已存在")
    Frontend --> User : 提示用户名已存在
else 注册成功
    Controller -> Controller : 创建Emp对象
    
    Controller -> EmpMapper : insert(emp)
    activate EmpMapper
    
    EmpMapper -> DB : INSERT INTO emp (username, password, name)
    activate DB
    DB --> EmpMapper : 插入成功
    deactivate DB
    
    EmpMapper --> Controller : 返回影响行数
    deactivate EmpMapper
    
    Controller -> StatsMapper : insert(userStats)
    activate StatsMapper
    
    StatsMapper -> DB : INSERT INTO user_stats (username)
    activate DB
    DB --> StatsMapper : 插入成功
    deactivate DB
    
    StatsMapper --> Controller : 返回结果
    deactivate StatsMapper
    
    Controller --> Frontend : Result.success("注册成功")
    deactivate Controller
    
    Frontend --> User : 显示注册成功，跳转登录
end

deactivate Frontend

@enduml
```

### 3.3 修改密码时序图

```plantuml
@startuml
skinparam backgroundColor #FEFEFE
skinparam defaultFontName Microsoft YaHei

actor 用户 as User
participant "前端页面" as Frontend
participant "AuthController" as Controller
participant "EmpMapper" as EmpMapper
database "MySQL数据库" as DB

User -> Frontend : 输入原密码和新密码
activate Frontend

Frontend -> Controller : POST /api/user/password
activate Controller
note right: Header: Authorization: Bearer token

Controller -> Controller : 验证Token

Controller -> EmpMapper : findById(userId)
activate EmpMapper

EmpMapper -> DB : SELECT * FROM emp WHERE id = ?
activate DB

DB --> EmpMapper : 返回用户信息
deactivate DB

EmpMapper --> Controller : 返回Emp对象
deactivate EmpMapper

alt 原密码错误
    Controller --> Frontend : Result.error("原密码错误")
    Frontend --> User : 提示原密码错误
else 新密码长度不足
    Controller --> Frontend : Result.error("新密码至少6位")
    Frontend --> User : 提示密码长度不足
else 修改成功
    Controller -> EmpMapper : updatePassword(id, newPassword)
    activate EmpMapper
    
    EmpMapper -> DB : UPDATE emp SET password = ? WHERE id = ?
    activate DB
    DB --> EmpMapper : 更新成功
    deactivate DB
    
    EmpMapper --> Controller : 返回影响行数
    deactivate EmpMapper
    
    Controller --> Frontend : Result.success("密码修改成功")
    deactivate Controller
    
    Frontend --> User : 提示密码修改成功
end

deactivate Frontend

@enduml
```

---

## 四、类职责说明

| 类名 | 类型 | 职责 |
|------|------|------|
| AuthController | 控制器 | 处理用户认证相关请求，包括登录、注册、登出、信息修改等 |
| Emp | 实体类 | 用户基本信息实体，存储用户名、密码、姓名等 |
| UserStats | 实体类 | 用户统计数据实体，记录浏览次数、收藏次数、预测次数等 |
| EmpMapper | 数据访问层 | 用户表数据访问，提供增删改查操作 |
| UserStatsMapper | 数据访问层 | 用户统计表数据访问 |
| Result | 响应类 | 统一API响应格式封装 |

---

## 五、API接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/login | POST | 用户登录 |
| /api/register | POST | 用户注册 |
| /api/logout | POST | 用户登出 |
| /api/user/info | GET | 获取用户信息 |
| /api/user/update | POST | 更新用户信息 |
| /api/user/password | POST | 修改密码 |
| /api/user/favorites | GET/POST | 获取/设置用户喜好 |
| /api/user/stats/increment | POST | 增加用户统计数据 |
