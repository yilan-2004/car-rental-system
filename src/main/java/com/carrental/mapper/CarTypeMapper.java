
package com.carrental.mapper;

import com.carrental.entity.CarType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface CarTypeMapper extends BaseMapper<CarType> {

    Integer countCarsByTypeId(@Param("typeId") Long typeId);
}
