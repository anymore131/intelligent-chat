package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.Log;
import cn.edu.zust.se.domain.query.LogQuery;
import cn.edu.zust.se.domain.vo.LogVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author anymore131
 * @since 2025-05-09
 */
public interface LogMapper extends BaseMapper<Log> {
    IPage<LogVO> pageByQuery(@Param("page")Page<LogVO> page,
                             @Param("query")LogQuery query);
}
