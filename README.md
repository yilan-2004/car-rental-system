# 汽车租赁系统

基于 Spring MVC + MyBatis-Plus 的汽车租赁管理平台，支持车辆管理、租赁订单全流程、系统公告和操作日志审计。

## 技术栈

Java 8 · Spring 5.3.25 · Spring MVC · MyBatis-Plus 3.5.3 · MySQL 8.0 · Druid 连接池 · JUnit 4

## 项目结构

```
car/
├── src/main/java/com/carrental/
│   ├── entity/                # 数据实体
│   │   ├── SysUser.java           # 用户（admin / user）
│   │   ├── Car.java               # 车辆信息
│   │   ├── CarType.java           # 车辆类型
│   │   ├── RentOrder.java         # 租赁订单
│   │   ├── Notice.java            # 系统公告
│   │   └── OperationLog.java      # 操作日志
│   └── mapper/                # MyBatis-Plus Mapper
│       ├── SysUserMapper.java
│       ├── CarMapper.java
│       ├── CarTypeMapper.java
│       ├── RentOrderMapper.java
│       ├── NoticeMapper.java
│       └── OperationLogMapper.java
├── src/main/resources/
│   ├── mapper/                # MyBatis XML 映射文件
│   ├── applicationContext.xml # Spring IoC 配置
│   ├── jdbc.properties        # 数据库连接配置
│   └── mybatis-config.xml     # MyBatis 全局配置
├── src/test/                  # 单元测试
├── car_rental.sql             # 数据库建表 + 种子数据脚本
└── pom.xml
```

## 数据库设计

共 6 张表，通过外键关联：

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `sys_user` | 用户表 | username, role(admin/user), status, id_card |
| `car_type` | 车辆类型 | type_name（经济型/舒适型/商务型/SUV/豪华型） |
| `car` | 车辆信息 | car_name, brand, model, car_no, price_per_day, status(可租/已租出/维修中/已下架) |
| `rent_order` | 租赁订单 | order_no, days, total_price, extra_price(逾期费), status(待审核→已通过→待还车→已完成) |
| `notice` | 系统公告 | title, content, status |
| `operation_log` | 操作日志 | admin_id, module, operation, request_method, ip |

## 核心功能

- **用户管理** — 注册登录、角色权限（管理员/普通用户）、账号状态控制
- **车辆管理** — 车辆信息 CRUD、按类型/品牌/状态筛选、车辆图片管理
- **订单流程** — 下单→审核→通过/拒绝→还车→完成，支持逾期费用计算
- **系统公告** — 管理员发布/隐藏公告通知
- **操作日志** — 记录管理员操作（模块、请求方法、IP）
- **逻辑删除** — 全表逻辑删除（deleted 字段），数据可恢复

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Tomcat 9+（或 IDE 内置服务器）

### 初始化数据库

```bash
mysql -u root -p < car_rental.sql
```

### 修改数据库连接

编辑 `src/main/resources/jdbc.properties`，配置你的数据库连接信息。

### 编译打包

```bash
mvn clean package
```

生成的 WAR 文件位于 `target/car-rental.war`，部署到 Tomcat 即可。

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 普通用户 | user1 / user2 | 123456 |

> 注意：测试数据中密码为明文，生产环境需替换为 BCrypt 加密。

## License

MIT
