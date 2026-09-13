# 瑞吉外卖 (reggie_take_out)

瑞吉外卖是专门为餐饮企业（餐厅、饭店）定制的一款软件产品，包括 **系统管理后台** 和 **移动端应用** 两部分。

本仓库基于《瑞吉外卖》课程讲义 **Day01 ~ Day06** 完成，当前进度覆盖：

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

## ✅ 已完成功能（Day04）

- **文件上传/下载**：`CommonController`（`POST /common/upload`、`GET /common/download`），图片存储目录配置于 `application.yml`（`reggie.path`），过滤器放行 `/common/**`；演示页 `backend/page/demo/upload.html`
- **菜品分类下拉查询**：`GET /category/list?type=1`（按 sort 升序、updateTime 倒序）
- **新增菜品**：`POST /dish`（`DishDto` 封装口味列表，同时写入 dish + dish_flavor 两张表，`@Transactional` 保证一致性，引导类开启事务）
- **菜品分页查询**：`GET /dish/page`（返回 `DishDto` 并封装分类名称 `categoryName`）
- **菜品修改**：`GET /dish/{id}` 回显（含口味）+ `PUT /dish` 修改（口味按“先删除后添加”更新）
- 配套新增 `DishFlavor` 实体/Mapper/Service、`dto/DishDto`、`DishController`

## ✅ 已完成功能（Day05）

- **按分类查询起售菜品**：`GET /dish/list?categoryId=`（新增套餐时选择菜品用，仅返回 status=1 并按 sort 升序）
- **新增套餐**：`POST /setmeal`（`SetmealDto` 封装套餐关联菜品，写入 setmeal + setmeal_dish 两张表，`@Transactional`）
- **套餐分页查询**：`GET /setmeal/page`（返回 `SetmealDto` 并封装 `categoryName`）
- **删除套餐**：`DELETE /setmeal?ids=`（支持单个/批量；**售卖中的套餐不允许删除**，抛 `CustomException` 提示；同时清理 setmeal_dish 关联数据）
- **短信发送**：引入阿里云短信 SDK，`utils/SMSUtils` 发送工具类（个人无法申请签名/模板，测试时验证码通过日志输出）
- **手机验证码登录（C端）**：`utils/ValidateCodeUtils` 生成 4 位验证码；`POST /user/sendMsg`（验证码存入 Session）、`POST /user/login`（校验验证码，新手机号自动注册 user 表并写入 Session）
- `LoginCheckFilter` 放行 `/user/sendMsg`、`/user/login`，并新增 **C 端登录态判定**（Session 中的 `user` → 写入 ThreadLocal 后放行）
- 前端适配：`front/api/login.js` 增加 `sendMsgApi`、`front/page/login.html` 接入发送验证码并携带 code 登录、`front/js/request.js` 未登录统一按 `NOTLOGIN` 跳转登录页

## ✅ 已完成功能（Day06）

- **用户地址簿**：`AddressBookController`（`POST /addressBook` 新增、`PUT /addressBook` 修改、`DELETE /addressBook?ids=` 删除、`GET /addressBook/{id}` 详情、`PUT /addressBook/default` 设置默认地址、`GET /addressBook/default` 查询默认地址、`GET /addressBook/list` 查询当前用户全部地址）；地址与当前登录用户（`BaseContext`）绑定，默认地址通过“先全部置 0、再置 1”实现
- **菜品展示（移动端）**：`GET /dish/list` 改为返回 `R<List<DishDto>>`，在菜品基础上封装**口味列表 flavors**与分类名称；`GET /setmeal/list` 按分类与状态查询套餐
- **购物车**：`POST /shoppingCart/add`（同一菜品/套餐只累加 number，不新增记录）、`GET /shoppingCart/list`（按创建时间升序）、`DELETE /shoppingCart/clean`（清空当前用户购物车）
- **下单**：`POST /order/submit` → `OrderService.submit`（`@Transactional`）：校验购物车非空、校验收货地址、用 `IdWorker` 生成订单号、`AtomicInteger` 累加总金额、写入 orders 一条 + order_detail 多条、下单后清空购物车
- 配套新增 `AddressBook` / `ShoppingCart` / `Orders` / `OrderDetail` 实体及其 Mapper、Service 分层

## 📁 项目结构

```
src/main/java/com/itheima/reggie/
├── common      # 通用结果 R、全局异常处理、Jackson 对象转换器、BaseContext(ThreadLocal)、MyMetaObjectHandler(公共字段填充)、CustomException
├── config      # WebMvcConfig(静态资源映射/消息转换器)、MybatisPlusConfig(分页插件)
├── controller  # EmployeeController、CategoryController、DishController、SetmealController、CommonController(文件上传下载)、UserController(C端登录)、AddressBookController、ShoppingCartController、OrderController
├── dto         # DishDto（菜品 + 口味 + 分类名称）、SetmealDto（套餐 + 关联菜品 + 分类名称）
├── entity      # Employee / Category / Dish / DishFlavor / Setmeal / SetmealDish / User / AddressBook / ShoppingCart / Orders / OrderDetail
├── filter      # LoginCheckFilter（后台员工 + C端用户登录校验，登录id写入ThreadLocal）
├── mapper      # 各实体对应的 Mapper
├── service     # 各模块 Service 及实现（含 OrderService.submit 下单事务）
├── utils       # SMSUtils(阿里云短信)、ValidateCodeUtils(验证码生成)
└── ReggieApplication
src/main/resources/
├── application.yml   # 端口、数据源、MyBatis-Plus 配置、reggie.path(图片目录)
├── backend           # 系统管理后台前端静态资源
├── front             # 移动端前端静态资源
└── reggie.sql        # 数据库脚本（11 张表）
dish-images/          # 菜品/套餐配图（文件名与数据库中 image 字段一致，含 manifest.json）
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

5. 移动端（C端）体验：

   - 登录页：<http://localhost:8080/front/page/login.html>
   - 输入手机号 → 点击“获取验证码” → **验证码打印在服务端控制台日志中**（形如 `code=5872`）→ 输入后登录
   - 登录后可体验点餐、地址簿、购物车与下单；菜品图片存放于 `reggie.path` 配置的目录

## 🖼️ 菜品图片（dish-images/）

`reggie.sql` 里菜品/套餐的 `image` 字段保存的是图片文件名（如 `f966a38e-....jpg`），但导入 SQL 时磁盘上并没有这些图片，所以菜品列表会出现“图片显示不出来”的情况。

本仓库 `dish-images/` 目录已补齐全部 **23 道菜品 + 1 个套餐** 的配图，**文件名与数据库中 `dish.image` / `setmeal.image` 的取值一一对应**（见 `dish-images/manifest.json`）。

使用方式（把图片放进 `application.yml` 中 `reggie.path` 指向的目录，默认为 `D:\img\`）：

```powershell
# Windows PowerShell
New-Item -ItemType Directory -Force D:\img | Out-Null
Copy-Item dish-images\* D:\img\ -Exclude manifest.json
```

之后启动项目，后台「菜品管理」列表与移动端点餐页即可正常显示菜品图片。

> 图片来自公开美食菜谱站点（下厨房等）的成品菜照片，仅用于课程演示与本地练习；如需商用请替换为自有素材或正版图库图片。

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
| 分类下拉列表 | GET `/category/list` | 参数 type(1菜品/2套餐) |
| 文件上传 | POST `/common/upload` | multipart，参数 file |
| 文件下载 | GET `/common/download` | 参数 name(文件名) |
| 新增菜品 | POST `/dish` | DishDto：基本信息 + flavors 口味列表 |
| 菜品分页查询 | GET `/dish/page` | 参数 page / pageSize / name，含 categoryName |
| 菜品详情回显 | GET `/dish/{id}` | 返回菜品 + 口味列表 |
| 修改菜品 | PUT `/dish` | 口味“先删后插”更新 |
| 按分类查询菜品 | GET `/dish/list` | 参数 categoryId，仅返回起售(status=1)菜品 |
| 新增套餐 | POST `/setmeal` | SetmealDto：套餐信息 + setmealDishes 关联菜品 |
| 套餐分页查询 | GET `/setmeal/page` | 参数 page / pageSize / name，含 categoryName |
| 删除套餐 | DELETE `/setmeal?ids=` | 支持批量；售卖中的套餐不可删除，同时清理关联表 |
| C端发送验证码 | POST `/user/sendMsg` | {phone}，验证码存 Session 并打印在服务端日志 |
| C端验证码登录 | POST `/user/login` | {phone, code}，新手机号自动注册 |
| 按分类查询套餐 | GET `/setmeal/list` | 参数 categoryId / status |
| 新增/修改/删除地址 | POST / PUT / DELETE `/addressBook` | 地址与当前登录用户绑定 |
| 设置默认地址 | PUT `/addressBook/default` | 先全部置 0，再当前置 1 |
| 查询默认地址 | GET `/addressBook/default` | 当前用户 is_default=1 的地址 |
| 地址列表 | GET `/addressBook/list` | 当前用户全部地址 |
| 加入购物车 | POST `/shoppingCart/add` | 同菜品/套餐累加数量 |
| 查看购物车 | GET `/shoppingCart/list` | 当前用户购物车 |
| 清空购物车 | DELETE `/shoppingCart/clean` | 按用户清空 |
| 用户下单 | POST `/order/submit` | {addressBookId, payMethod, remark}，写订单+明细并清空购物车 |

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
