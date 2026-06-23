
package com.carrental.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface OperationLogMapper extends BaseMapper<OperationLog> {

    IPage<OperationLog> selectLogPage(
            Page<OperationLog> page,
            @Param("adminName") String adminName,
            @Param("module") String module
    );
}
