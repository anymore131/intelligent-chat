package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.SystemUsed;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author anymore131
 * @since 2025-03-26
 */
public interface SystemUsedMapper extends BaseMapper<SystemUsed> {
    @Select("select * from system_used where memory_id = #{memoryId}")
    SystemUsed getByMemoryId(String memoryId);
}
