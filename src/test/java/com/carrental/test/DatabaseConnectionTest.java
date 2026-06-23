package com.carrental.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class DatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() throws Exception {
        System.out.println("===== 测试数据库连接 =====");
        
        Properties props = loadJdbcProperties();
        String driver = props.getProperty("jdbc.driver");
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        
        Class.forName(driver);
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Assert.assertNotNull("数据库连接对象不能为空", connection);
            Assert.assertFalse("数据库连接不应处于关闭状态", connection.isClosed());
            
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("数据库名称: " + metaData.getDatabaseProductName());
            System.out.println("数据库版本: " + metaData.getDatabaseProductVersion());
            System.out.println("驱动名称: " + metaData.getDriverName());
            System.out.println("数据库连接测试通过");
        }
    }

    @Test
    public void testSimpleSql() throws Exception {
        System.out.println("===== 测试简单SQL =====");
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {
            
            Assert.assertNotNull("ResultSet 不应为空", resultSet);
            Assert.assertTrue("SELECT 1 应返回一行数据", resultSet.next());
            Assert.assertEquals("SELECT 1 的结果应为 1", 1, resultSet.getInt(1));
            System.out.println("简单SQL测试通过");
        }
    }

    @Test
    public void testCurrentDatabase() throws Exception {
        System.out.println("===== 测试当前数据库 =====");
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT DATABASE()");
             ResultSet resultSet = statement.executeQuery()) {
            
            Assert.assertTrue("应能获取数据库名称", resultSet.next());
            String databaseName = resultSet.getString(1);
            Assert.assertNotNull("数据库名称不应为空", databaseName);
            Assert.assertEquals("当前数据库名称不正确", "car_rental", databaseName);
            System.out.println("当前数据库: " + databaseName);
            System.out.println("数据库名称测试通过");
        }
    }

    @Test
    public void testRequiredTablesExist() throws Exception {
        System.out.println("===== 测试必需数据表 =====");
        
        List<String> requiredTables = Arrays.asList(
            "sys_user",
            "car_type",
            "car",
            "rent_order",
            "notice",
            "operation_log"
        );
        
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseName = connection.getCatalog();
            
            Set<String> actualTables = new HashSet<>();
            try (ResultSet tables = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME").toLowerCase();
                    actualTables.add(tableName);
                }
            }
            
            System.out.println("检测到的表名: " + actualTables);
            
            for (String tableName : requiredTables) {
                Assert.assertTrue("缺少数据表：" + tableName, actualTables.contains(tableName));
            }
            
            System.out.println("所有必需数据表测试通过");
        }
    }

    @Test
    public void testInitialData() throws Exception {
        System.out.println("===== 测试基础数据 =====");
        
        try (Connection connection = getConnection()) {
            int userCount = queryCount(connection, "SELECT COUNT(*) FROM sys_user");
            int typeCount = queryCount(connection, "SELECT COUNT(*) FROM car_type");
            int carCount = queryCount(connection, "SELECT COUNT(*) FROM car");
            int noticeCount = queryCount(connection, "SELECT COUNT(*) FROM notice");
            
            System.out.println("用户表数据数量: " + userCount);
            System.out.println("车辆类型表数据数量: " + typeCount);
            System.out.println("车辆表数据数量: " + carCount);
            System.out.println("公告表数据数量: " + noticeCount);
            
            Assert.assertTrue("用户表至少应有1条数据", userCount > 0);
            Assert.assertTrue("车辆类型表至少应有1条数据", typeCount > 0);
            Assert.assertTrue("车辆表至少应有1条数据", carCount > 0);
            Assert.assertTrue("公告表至少应有1条数据", noticeCount > 0);
            
            System.out.println("基础数据测试通过");
        }
    }

    @Test
    public void testAdminUserExists() throws Exception {
        System.out.println("===== 测试管理员用户 =====");
        
        String sql = "SELECT id, username, role, status FROM sys_user WHERE username = ? AND deleted = 0";
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "admin");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                Assert.assertTrue("管理员用户 admin 不存在", resultSet.next());
                
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");
                int status = resultSet.getInt("status");
                
                Assert.assertEquals("用户名应为 admin", "admin", username);
                Assert.assertEquals("角色应为 admin", "admin", role);
                Assert.assertEquals("状态应为 1", 1, status);
                
                System.out.println("管理员用户存在: username=" + username + ", role=" + role + ", status=" + status);
                System.out.println("管理员用户测试通过");
            }
        }
    }

    private Properties loadJdbcProperties() {
        Properties props = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jdbc.properties")) {
            Assert.assertNotNull("未找到 jdbc.properties 配置文件", inputStream);
            props.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("加载 jdbc.properties 失败: " + e.getMessage(), e);
        }
        return props;
    }

    private Connection getConnection() throws Exception {
        Properties props = loadJdbcProperties();
        String driver = props.getProperty("jdbc.driver");
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    private int queryCount(Connection connection, String sql) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }
}
