
package com.carrental.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.RentOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface RentOrderMapper extends BaseMapper<RentOrder> {

    RentOrder selectOrderDetailById(@Param("id") Long id);

    IPage<RentOrder> selectOrderPage(
            Page<RentOrder> page,
            @Param("orderNo") String orderNo,
            @Param("username") String username,
            @Param("carName") String carName,
            @Param("status") Integer status
    );

    IPage<RentOrder> selectUserOrderPage(
            Page<RentOrder> page,
            @Param("userId") Long userId,
            @Param("status") Integer status
    );

    Integer countEffectiveOrdersByCarId(@Param("carId") Long carId);
}
