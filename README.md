# 瑞吉外卖 (reggie_take_out)

瑞吉外卖是专门为餐饮企业（餐厅、饭店）定制的一款软件产品，包括 **系统管理后台** 和 **移动端应用** 两部分。

本仓库基于《瑞吉外卖》课程讲义 **Day01 / Day02 / Day03** 完成，当前进度覆盖：

## ✅ 已完成功能（Day01）

- 开发环境搭建（Maven + Spring Boot 2.4.5 + MyBatis-Plus + MySQL + Druid）
- 前端静态资源导入与访问（`backend` / `front`）
- 后台系统**员工登录**功能
- 后台系统**退出**功能

## ✅ 已完成功能（Day02）

- **完善登录功能**：登录校验过滤器 `LoginCheckFilter` —— 未登录访问后台页面直接重定向到登录页，访问业务接口返回 `NOTLOGIN` 由前端跳转登录页
- **新增员工**（初始密码 `123456`，MD5 加密）及全局异常处理（用户名重复提示）
- **员工信息分页查询**（MyBatis-Plus 分页插件，支持按姓名模糊查询）
- **启用/禁用员工账号**（仅管理员 admin 可操作，页面按钮 + 后端接口双重校验）
- **编辑员工信息**（根据 id 回显 + 修改保存；权限控制见下方“权限说明”）
- 解决 Long 型 id 前端精度丢失问题（`JacksonObjectMapper` 将 Long/BigInteger 序列化为字符串）

## ✅ 已完成功能（Day03）

- **公共字段自动填充**：`MyMetaObjectHandler` + `BaseContext`（ThreadLocal）在插入/更新时统一填充 `createTime/updateTime/createUser/updateUser`，当前登录用户 id 由 `LoginCheckFilter` 写入 ThreadLocal 动态获取
- **新增分类**（菜品分类 type=1 / 套餐分类 type=2），分类名称唯一校验
- **分类信息分页查询**（按 sort 升序）
- **删除分类**：删除前校验是否关联菜品/套餐（`CustomException` 自定义业务异常 + 全局异常处理）
- **修改分类**
- 配套新增 `Category` / `Dish` / `Setmeal` 实体及其 Mapper / Service 分层

## 📁 项目结构

```
src/main/java/com/itheima/reggie/
├── common      # 通用结果 R、全局异常处理、Jackson 对象转换器、BaseContext(ThreadLocal)、MyMetaObjectHandler(公共字段填充)、CustomException
├── config      # WebMvcConfig(静态资源映射/消息转换器)、MybatisPlusConfig(分页插件)
├── controller  # EmployeeController（员工登录/退出/新增/分页/修改/编辑回显）、CategoryController（分类增删改查）
├── entity      # Employee / Category / Dish / Setmeal
├── filter      # LoginCheckFilter（登录校验过滤器，登录用户id写入ThreadLocal）
├── mapper      # EmployeeMapper / CategoryMapper / DishMapper / SetmealMapper
├── service     # Employee/Category/Dish/Setmeal Service 及实现
└── ReggieApplication
src/main/resources/
├── application.yml   # 端口、数据源、MyBatis-Plus 配置
├── backend           # 系统管理后台前端静态资源
├── front             # 移动端前端静态资源
└── reggie.sql        # 数据库脚本（11 张表）
```

## 🚀 运行步骤

1. 本地安装并启动 MySQL（8.x），创建数据库并导入脚本：

   ```sql
   CREATE DATABASE reggie DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
   -- 导入 reggie.sql
   ```

2. 修改 `src/main/resources/application.yml` 中数据库的 `username` / `password`（默认 `root/123456`）。

3. 使用 JDK 1.8 启动主类 `com.itheima.reggie.ReggieApplication`。

4. 浏览器访问管理后台：

   - 登录页：<http://localhost:8080/backend/page/login/login.html>
   - 默认账号：`admin`，密码：`123456`

## ⚙️ 主要接口

| 功能 | 请求 | 说明 |
| --- | --- | --- |
| 员工登录 | POST `/employee/login` | 用户名 + 密码（MD5） |
| 员工退出 | POST `/employee/logout` | 清理 Session |
| 新增员工 | POST `/employee` | 初始密码 123456 |
| 员工分页查询 | GET `/employee/page` | 参数 page / pageSize / name |
| 启用/禁用/修改员工 | PUT `/employee` | id + status / 员工信息 |
| 根据 id 查询员工 | GET `/employee/{id}` | 编辑回显 |
| 新增分类 | POST `/category` | {name, type, sort} |
| 分类分页查询 | GET `/category/page` | 参数 page / pageSize，按 sort 升序 |
| 删除分类 | DELETE `/category?ids=` | 关联菜品/套餐时提示不可删除 |
| 修改分类 | PUT `/category` | {id, name, sort} |

## 🔐 权限说明

- **管理员（admin）**：拥有完整权限，可新增员工、修改任何员工资料、启用/禁用任何员工账号。
- **普通员工**：
  - 前端：员工列表页不显示“编辑 / 启用 / 禁用”按钮；
  - 后端（`PUT /employee` 接口校验，基于 `BaseContext` 中的当前登录用户）：
    - 不允许修改其他员工（含管理员）的信息；
    - 不允许修改任何员工的账号状态（含自己），防止越权禁用；
    - 仅允许修改自己的资料（姓名/手机号等非状态字段）。
- 登录/退出、菜品与分类等业务接口对所有已登录员工开放。

> 技术栈：Spring Boot 2.4.5 · Spring MVC · MyBatis-Plus 3.4.2 · MySQL · Druid · Lombok · Fastjson
