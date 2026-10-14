# GradMate AdminSystem

**GradMate 考研助手后台管理系统**，用于集中维护院校与专业、招生资讯、用户数据、学习记录及 AI 会话等业务信息，并提供管理员鉴权、数据操作保护和审计日志。

项目采用 **Vue 3 + Spring Boot** 前后端分离架构，与 GradMate 考研助手小程序共用 `gradmate` 业务数据库。本仓库文档聚焦**管理员系统**，不代表小程序端的完整实现。

## 功能特性

| 模块 | 主要功能 |
| --- | --- |
| 仪表盘 | 查看用户、院校、专业、资讯、学习记录及 AI 会话等统计数据 |
| 基础数据 | 院校管理、专业管理、专业分类；维护院校标签（如双一流、研究生院、自划线） |
| 资讯内容 | 管理招生简章、调剂通知、校园资讯及相关链接 |
| 用户数据 | 管理用户账号、收藏记录和浏览历史 |
| 学习与 AI | 管理学习任务、学习记录、AI 会话和 AI 消息 |
| 系统管理 | 管理后台管理员账号，查看与筛选系统操作日志 |

通用资源管理页面支持**关键词搜索、条件筛选、分页、新增、编辑和删除**。针对重点业务对象，后端在删除前检查关联数据，避免误删影响小程序端。

### 设计亮点

- **管理员鉴权**：使用 JWT 认证与 BCrypt 密码校验；受保护接口要求 `Bearer Token`。
- **元数据驱动 CRUD**：通过 `AdminMetaRegistry` 登记允许管理的资源、字段和筛选条件，限制任意表和字段访问。
- **关联删除保护**：`DeleteGuardService` 对院校、用户和管理员等重点对象进行检查。
- **操作审计**：记录登录、退出、创建、更新、删除及删除日志等关键操作，支持按条件查询与查看详情。
- **敏感数据保护**：审计日志不保存请求体；管理员账户至少保留一个启用账号。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite 5、Element Plus、Vue Router、Axios |
| 后端 | Java 17、Spring Boot 3.2.2、Maven |
| 数据访问 | Spring JDBC（`JdbcTemplate`）、MyBatis Plus 3.5.7 |
| 数据库 | MySQL 8.x（`gradmate`） |
| 安全 | JWT、BCrypt |

## 项目结构

以下为管理员系统的主要目录（以实际仓库布局为准）：

```text
GradMate_AdminSystem/
├── GradMate_AdminSystem_SpringBoot/   # Spring Boot 后端
├── GradMate_AdminSystem_Vue3/         # Vue 3 前端
└── gradmate.sql                       # 数据库结构脚本
```

**后端关键模块**：`common`（通用响应与异常处理）、`config`（鉴权与配置）、`controller`（API）、`service`（业务逻辑）、`entity / mapper`（数据映射）、`util`（JWT 与密码工具）。

**前端关键文件**：

- `src/router/index.js`：路由与登录守卫。
- `src/api/request.js`：Axios 请求封装、Token 注入及未授权处理。
- `src/config/resources.js`：菜单、资源字段、筛选项和表单配置。
- `src/views/AdminLayout.vue`：后台布局。
- `src/views/ResourcePage.vue`：通用数据管理页面。
- `src/views/LogPage.vue`：系统日志页面。

## 快速开始

### 1. 环境准备

安装 **JDK 17、Maven、Node.js/npm、MySQL 8.x**。Node.js 版本请使用与项目 Vite 5 兼容的版本。

### 2. 初始化数据库

根据项目中的 `gradmate.sql` 初始化数据库，默认数据库名为 `gradmate`。**导入前请确认脚本内容，避免覆盖已有数据。**

后端数据库连接配置位于：

```text
GradMate_AdminSystem_SpringBoot/src/main/resources/application.properties
```

请按本地环境配置 MySQL 连接信息，并为 JWT 配置安全的密钥（配置项 `admin.jwt.secret`）。不要将实际数据库密码或密钥提交到公共仓库。

### 3. 启动后端

```bash
cd GradMate_AdminSystem/GradMate_AdminSystem_SpringBoot
mvn spring-boot:run
```

开发环境默认后端地址：`http://127.0.0.1:7070/`

### 4. 启动前端

在另一个终端执行：

```bash
cd GradMate_AdminSystem/GradMate_AdminSystem_Vue3
npm install
npm run dev -- --host 127.0.0.1
```

开发环境默认前端地址：`http://127.0.0.1:5173/`

使用部署者提供的管理员账号登录。若前后端无法连通，请核对 `vite.config.js` 和 `src/api/request.js` 中的接口访问配置。

> 上述路径假设从包含 `GradMate_AdminSystem` 的上级目录执行；若已进入该目录，请省略命令开头的 `GradMate_AdminSystem/`。

### 5. 测试与构建

后端（在 Spring Boot 项目目录执行）：

```bash
mvn test
mvn -DskipTests package
```

前端（在 Vue 3 项目目录执行）：

```bash
npm run build
```

## 主要 API

管理接口统一使用 `/api/admin` 前缀。

| 方法 | 接口 | 功能 |
| --- | --- | --- |
| `POST` | `/api/admin/auth/login` | 管理员登录 |
| `GET` | `/api/admin/auth/profile` | 获取当前管理员信息 |
| `POST` | `/api/admin/auth/logout` | 退出登录 |
| `GET` | `/api/admin/dashboard/stats` | 仪表盘统计 |
| `GET` | `/api/admin/{resource}` | 资源分页查询 |
| `POST` | `/api/admin/{resource}` | 新增资源 |
| `PUT` | `/api/admin/{resource}/{id}` | 编辑资源 |
| `DELETE` | `/api/admin/{resource}/{id}` | 删除资源（按需执行关联检查） |
| `GET/POST/PUT/DELETE` | `/api/admin/infos/{kind}` | 资讯管理 |
| `GET/POST/PUT/DELETE` | `/api/admin/study/{kind}` | 学习数据管理 |
| `GET/POST/PUT/DELETE` | `/api/admin/ai/{kind}` | AI 会话与消息管理 |
| `GET` | `/api/admin/logs` | 查询操作日志 |
| `GET` | `/api/admin/logs/{id}` | 查看日志详情 |
| `DELETE` | `/api/admin/logs/{id}` | 删除单条日志并记录 `DELETE_LOG` |

常见资源包括 `schools`、`majors`、`major-categories`、`users` 和 `admins`。分页查询支持 `pageNum`、`pageSize`、`keyword` 及各模块筛选条件。

统一响应结构：

```json
{
  "code": 200,
  "message": "操作结果信息",
  "data": {}
}
```

## 数据关系与开发注意事项

- **院校与专业**：`major.school_name` 与院校名称为逻辑关联；修改院校名称时应检查相关专业数据。
- **院校与资讯**：`school_brochure`、`school_adjust`、`school_information` 通过 `school_id` 关联院校。
- **用户相关数据**：学习任务、学习记录、收藏、浏览历史及 AI 会话等可能引用用户记录。
- **关联约束**：当前部分业务关系依赖逻辑关联而非数据库外键，维护或扩展时应保留后端校验与删除保护。
- **审计规则**：关键写操作和认证事件写入日志；普通 GET 查询不记录操作日志，日志中不保存请求体。

新增管理模块时，建议先扩展后端 `AdminMetaRegistry`，再同步修改前端 `src/config/resources.js`；涉及删除逻辑或重要写操作时，分别完善 `DeleteGuardService` 与 `AdminOperationLogService`。


## 项目文档

详细的操作流程与实现说明可参阅：

- 《GradMate 管理员系统用户使用说明书》：登录、数据维护、业务模块与常见问题。
- 《GradMate 管理员系统程序员说明书》：技术架构、数据表关系、接口、日志实现与构建测试。

