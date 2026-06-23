
package com.carrental.mapper;

import com.carrental.entity.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class MapperTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CarTypeMapper carTypeMapper;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private RentOrderMapper rentOrderMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Test
    public void testUserMapper() {
        System.out.println("===== 测试用户Mapper =====");
        
        List<SysUser> userList = sysUserMapper.selectList(null);
        Assert.assertNotNull("用户列表不应为空", userList);
        Assert.assertFalse("用户列表不应为空", userList.isEmpty());
        System.out.println("用户列表大小: " + userList.size());
        for (SysUser user : userList) {
            System.out.println("用户ID: " + user.getId() + ", 用户名: " + user.getUsername() + ", 真实姓名: " + user.getRealName());
        }

        SysUser admin = sysUserMapper.selectByUsername("admin");
        Assert.assertNotNull("管理员用户不应为空", admin);
        Assert.assertEquals("用户名为admin", "admin", admin.getUsername());
        System.out.println("管理员用户: " + admin.getUsername() + ", 角色: " + admin.getRole());
    }

    @Test
    public void testCarTypeMapper() {
        System.out.println("===== 测试车辆类型Mapper =====");
        
        List<CarType> carTypeList = carTypeMapper.selectList(null);
        Assert.assertNotNull("车辆类型列表不应为空", carTypeList);
        Assert.assertFalse("车辆类型列表不应为空", carTypeList.isEmpty());
        System.out.println("车辆类型列表大小: " + carTypeList.size());
        for (CarType carType : carTypeList) {
            System.out.println("类型ID: " + carType.getId() + ", 类型名称: " + carType.getTypeName() + ", 描述: " + carType.getDescription());
        }
    }

    @Test
    public void testCarMapper() {
        System.out.println("===== 测试车辆Mapper =====");
        
        List<Car> carList = carMapper.selectList(null);
        Assert.assertNotNull("车辆列表不应为空", carList);
        Assert.assertFalse("车辆列表不应为空", carList.isEmpty());
        System.out.println("车辆列表大小: " + carList.size());
        for (Car car : carList) {
            System.out.println("车辆ID: " + car.getId() + ", 车辆名称: " + car.getCarName() + ", 品牌: " + car.getBrand() + ", 车牌号: " + car.getCarNo());
        }

        if (!carList.isEmpty()) {
            Long firstCarId = carList.get(0).getId();
            Car carDetail = carMapper.selectCarDetailById(firstCarId);
            Assert.assertNotNull("车辆详情不应为空", carDetail);
            System.out.println("车辆详情 - 车辆名称: " + carDetail.getCarName() + ", 类型名称: " + carDetail.getTypeName());
        }
    }

    @Test
    public void testRentOrderMapper() {
        System.out.println("===== 测试订单Mapper =====");
        
        List<RentOrder> orderList = rentOrderMapper.selectList(null);
        Assert.assertNotNull("订单列表不应为空", orderList);
        Assert.assertFalse("订单列表不应为空", orderList.isEmpty());
        System.out.println("订单列表大小: " + orderList.size());
        for (RentOrder order : orderList) {
            System.out.println("订单ID: " + order.getId() + ", 订单编号: " + order.getOrderNo() + ", 状态: " + order.getStatus());
        }

        if (!orderList.isEmpty()) {
            Long firstOrderId = orderList.get(0).getId();
            RentOrder orderDetail = rentOrderMapper.selectOrderDetailById(firstOrderId);
            Assert.assertNotNull("订单详情不应为空", orderDetail);
            System.out.println("订单详情 - 订单编号: " + orderDetail.getOrderNo() + ", 用户名: " + orderDetail.getUsername() + ", 车辆名称: " + orderDetail.getCarName());
        }
    }

    @Test
    public void testNoticeMapper() {
        System.out.println("===== 测试公告Mapper =====");
        
        List<Notice> noticeList = noticeMapper.selectList(null);
        Assert.assertNotNull("公告列表不应为空", noticeList);
        Assert.assertFalse("公告列表不应为空", noticeList.isEmpty());
        System.out.println("公告列表大小: " + noticeList.size());
        for (Notice notice : noticeList) {
            System.out.println("公告ID: " + notice.getId() + ", 标题: " + notice.getTitle() + ", 状态: " + notice.getStatus());
        }

        List<Notice> visibleNotices = noticeMapper.selectVisibleNotices();
        Assert.assertNotNull("可见公告列表不应为空", visibleNotices);
        Assert.assertFalse("可见公告列表不应为空", visibleNotices.isEmpty());
        System.out.println("可见公告列表大小: " + visibleNotices.size());
        for (Notice notice : visibleNotices) {
            Assert.assertEquals("公告状态应为1", Integer.valueOf(1), notice.getStatus());
            System.out.println("可见公告标题: " + notice.getTitle());
        }
    }

    @Test
    public void testOperationLogMapper() {
        System.out.println("===== 测试操作日志Mapper =====");
        
        List<OperationLog> logList = operationLogMapper.selectList(null);
        Assert.assertNotNull("操作日志列表不应为空", logList);
        System.out.println("操作日志列表大小: " + logList.size());
        for (OperationLog log : logList) {
            System.out.println("日志ID: " + log.getId() + ", 管理员ID: " + log.getAdminId() + ", 模块: " + log.getModule() + ", 操作: " + log.getOperation());
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testNoticeInsertAndDelete() {
        System.out.println("===== 测试公告新增和删除 =====");
        
        Notice notice = new Notice();
        notice.setTitle("测试公告");
        notice.setContent("这是 Mapper 功能测试数据");
        notice.setStatus(1);

        int insertResult = noticeMapper.insert(notice);
        Assert.assertEquals("插入应返回1", 1, insertResult);
        Assert.assertNotNull("主键应自动生成", notice.getId());
        System.out.println("新增公告ID: " + notice.getId());

        Notice insertedNotice = noticeMapper.selectById(notice.getId());
        Assert.assertNotNull("根据主键应能查询到公告", insertedNotice);
        Assert.assertEquals("标题应匹配", "测试公告", insertedNotice.getTitle());
        Assert.assertEquals("内容应匹配", "这是 Mapper 功能测试数据", insertedNotice.getContent());
        Assert.assertEquals("状态应为1", Integer.valueOf(1), insertedNotice.getStatus());

        int deleteResult = noticeMapper.deleteById(notice.getId());
        Assert.assertEquals("删除应返回1", 1, deleteResult);

        Notice deletedNotice = noticeMapper.selectById(notice.getId());
        Assert.assertNull("删除后应查询不到公告", deletedNotice);
        System.out.println("公告删除测试完成");
    }
}
