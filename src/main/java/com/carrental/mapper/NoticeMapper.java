
package com.carrental.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.entity.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeMapper extends BaseMapper<Notice> {

    IPage<Notice> selectNoticePage(
            Page<Notice> page,
            @Param("title") String title,
            @Param("status") Integer status
    );

    List<Notice> selectVisibleNotices();
}
