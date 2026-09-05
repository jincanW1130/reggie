# 瑞吉外卖 (reggie_take_out)

瑞吉外卖是专门为餐饮企业（餐厅、饭店）定制的一款软件产品，包括 **系统管理后台** 和 **移动端应用** 两部分。

本仓库基于《瑞吉外卖》课程讲义 **Day01 / Day02** 完成，当前进度覆盖：

## ✅ 已完成功能（Day01）

- 开发环境搭建（Maven + Spring Boot 2.4.5 + MyBatis-Plus + MySQL + Druid）
- 前端静态资源导入与访问（`backend` / `front`）
- 后台系统**员工登录**功能
- 后台系统**退出**功能

## ✅ 已完成功能（Day02）

- **完善登录功能**：登录校验过滤器 `LoginCheckFilter` —— 未登录访问后台页面直接重定向到登录页，访问业务接口返回 `NOTLOGIN` 由前端跳转登录页
- **新增员工**（初始密码 `123456`，MD5 加密）及全局异常处理（用户名重复提示）
- **员工信息分页查询**（MyBatis-Plus 分页插件，支持按姓名模糊查询）
- **启用/禁用员工账号**（仅管理员 admin 可见操作）
- **编辑员工信息**（根据 id 回显 + 修改保存）
- 解决 Long 型 id 前端精度丢失问题（`JacksonObjectMapper` 将 Long/BigInteger 序列化为字符串）

## 📁 项目结构

```
src/main/java/com/itheima/reggie/
├── common      # 通用结果 R、全局异常处理、Jackson 对象转换器
├── config      # WebMvcConfig(静态资源映射/消息转换器)、MybatisPlusConfig(分页插件)
├── controller  # EmployeeController（登录/退出/新增/分页/修改/编辑回显）
├── entity      # Employee
├── filter      # LoginCheckFilter（登录校验过滤器）
├── mapper      # EmployeeMapper
├── service     # EmployeeService / EmployeeServiceImpl
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

2. 修改 `src/main/resources/application.yml` 中数据库的 `username` / `password`（默认 `root/root`）。

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

> 技术栈：Spring Boot 2.4.5 · Spring MVC · MyBatis-Plus 3.4.2 · MySQL · Druid · Lombok · Fastjson
