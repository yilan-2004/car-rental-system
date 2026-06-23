
package com.carrental.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Car;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface CarMapper extends BaseMapper<Car> {

    IPage<Car> selectCarPage(
            Page<Car> page,
            @Param("keyword") String keyword,
            @Param("typeId") Long typeId,
            @Param("status") Integer status
    );

    Car selectCarDetailById(@Param("id") Long id);

    Integer countActiveOrdersByCarId(@Param("carId") Long carId);
}
