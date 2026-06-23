-- 汽车租赁系统数据库脚本
-- 数据库名称: car_rental
-- 技术栈: Java 8 + Spring + Spring MVC + MyBatis-Plus 3.5.x + MySQL 8

-- ===========================================
-- 1. 创建数据库
-- ===========================================
DROP DATABASE IF EXISTS car_rental;
CREATE DATABASE car_rental 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_general_ci;

USE car_rental;

-- ===========================================
-- 2. 删除旧表（按外键依赖顺序删除）
-- ===========================================
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS rent_order;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS car_type;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS sys_user;

-- ===========================================
-- 3. 创建数据表
-- ===========================================

-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    id_card VARCHAR(18) COMMENT '身份证号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL COMMENT '角色: admin/user',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 车辆类型表
CREATE TABLE car_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '类型ID',
    type_name VARCHAR(50) NOT NULL COMMENT '类型名称',
    description VARCHAR(255) COMMENT '类型描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    UNIQUE KEY uk_type_name (type_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='车辆类型表';

-- 车辆信息表
CREATE TABLE car (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '车辆ID',
    car_name VARCHAR(100) NOT NULL COMMENT '车辆名称',
    brand VARCHAR(50) NOT NULL COMMENT '品牌',
    model VARCHAR(50) NOT NULL COMMENT '型号',
    car_no VARCHAR(20) NOT NULL COMMENT '车牌号',
    type_id BIGINT NOT NULL COMMENT '车辆类型ID',
    price_per_day DECIMAL(10,2) NOT NULL COMMENT '日租金',
    seat_num INT NOT NULL COMMENT '座位数',
    image VARCHAR(255) COMMENT '车辆图片路径',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 1可租, 2已租出, 3维修中, 4已下架',
    description VARCHAR(500) COMMENT '车辆描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    INDEX idx_car_name (car_name),
    INDEX idx_brand (brand),
    INDEX idx_type_id (type_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_car_no (car_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='车辆信息表';

-- 租车订单表
CREATE TABLE rent_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    car_id BIGINT NOT NULL COMMENT '车辆ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    return_time DATETIME COMMENT '实际归还时间',
    days INT NOT NULL COMMENT '租赁天数',
    total_price DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    extra_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '逾期费用',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态: 0待审核, 1已通过, 2已拒绝, 3待还车审核, 4已完成, 5已取消',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_car_id (car_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='租车订单表';

-- 系统公告表
CREATE TABLE notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    title VARCHAR(100) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 1显示, 0隐藏',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统公告表';

-- 管理员操作日志表
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    module VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation VARCHAR(100) NOT NULL COMMENT '操作内容',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(255) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT '操作IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_admin_id (admin_id),
    INDEX idx_module (module),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员操作日志表';

-- ===========================================
-- 4. 创建外键约束
-- ===========================================
ALTER TABLE car ADD CONSTRAINT fk_car_type_id FOREIGN KEY (type_id) REFERENCES car_type(id);
ALTER TABLE rent_order ADD CONSTRAINT fk_order_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id);
ALTER TABLE rent_order ADD CONSTRAINT fk_order_car_id FOREIGN KEY (car_id) REFERENCES car(id);
ALTER TABLE operation_log ADD CONSTRAINT fk_log_admin_id FOREIGN KEY (admin_id) REFERENCES sys_user(id);

-- ===========================================
-- 5. 初始化数据
-- ===========================================

-- 初始化用户数据
-- 注意：密码使用明文仅用于测试，正式项目需要使用BCrypt加密
INSERT INTO sys_user (username, password, real_name, phone, id_card, email, role, status, create_time) VALUES
('admin', '123456', '管理员', '13800138000', '110101199001011234', 'admin@carrental.com', 'admin', 1, NOW()),
('user1', '123456', '张三', '13900139001', '110101199102022345', 'user1@carrental.com', 'user', 1, NOW()),
('user2', '123456', '李四', '13900139002', '110101199203033456', 'user2@carrental.com', 'user', 1, NOW());

-- 初始化车辆类型数据
INSERT INTO car_type (type_name, description, create_time) VALUES
('经济型', '经济实惠，适合日常出行', NOW()),
('舒适型', '空间宽敞，乘坐舒适', NOW()),
('商务型', '高端大气，适合商务接待', NOW()),
('SUV', '越野性能强，适合户外出行', NOW()),
('豪华型', '豪华配置，尊贵体验', NOW());

-- 初始化车辆数据
INSERT INTO car (car_name, brand, model, car_no, type_id, price_per_day, seat_num, image, status, description, create_time) VALUES
('大众朗逸', '大众', '朗逸 2023款', '京A12345', 1, 150.00, 5, 'images/car1.jpg', 1, '大众朗逸，经济实惠，油耗低', NOW()),
('丰田凯美瑞', '丰田', '凯美瑞 2023款', '京B23456', 2, 280.00, 5, 'images/car2.jpg', 1, '丰田凯美瑞，舒适大气', NOW()),
('奔驰E级', '奔驰', 'E级 2023款', '京C34567', 3, 680.00, 5, 'images/car3.jpg', 1, '奔驰E级，商务首选', NOW()),
('本田CR-V', '本田', 'CR-V 2023款', '京D45678', 4, 320.00, 5, 'images/car4.jpg', 2, '本田CR-V，空间大，适合家庭出行', NOW()),
('宝马7系', '宝马', '7系 2023款', '京E56789', 5, 1200.00, 5, 'images/car5.jpg', 1, '宝马7系，豪华旗舰', NOW());

-- 初始化公告数据
INSERT INTO notice (title, content, status, create_time) VALUES
('系统维护通知', '尊敬的用户，本系统将于2024年1月1日00:00-02:00进行系统维护，期间将暂停服务，请您提前做好安排。', 1, NOW()),
('新用户优惠活动', '新用户首次租车可享受8折优惠，活动时间：2024年1月1日-2024年3月31日。', 1, NOW());

-- 初始化订单数据
INSERT INTO rent_order (order_no, user_id, car_id, start_time, end_time, return_time, days, total_price, extra_price, status, remark, create_time) VALUES
('ORD202401010001', 2, 4, '2024-01-05 09:00:00', '2024-01-07 18:00:00', '2024-01-07 17:30:00', 3, 960.00, 0.00, 4, '用户张三租用本田CR-V', NOW()),
('ORD202401010002', 3, 1, '2024-01-10 09:00:00', '2024-01-12 18:00:00', NULL, 3, 450.00, 0.00, 1, '用户李四租用大众朗逸', NOW());

-- ===========================================
-- 6. 表关系说明
-- ===========================================
-- car.type_id -> car_type.id (车辆类型关联)
-- rent_order.user_id -> sys_user.id (订单用户关联)
-- rent_order.car_id -> car.id (订单车辆关联)
-- operation_log.admin_id -> sys_user.id (操作日志管理员关联)

-- ===========================================
-- 7. 状态字段说明
-- ===========================================
-- sys_user.status: 1-正常, 0-禁用
-- car.status: 1-可租, 2-已租出, 3-维修中, 4-已下架
-- rent_order.status: 0-待审核, 1-已通过, 2-已拒绝, 3-待还车审核, 4-已完成, 5-已取消
-- notice.status: 1-显示, 0-隐藏