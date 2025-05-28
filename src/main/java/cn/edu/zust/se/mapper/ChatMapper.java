package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;

public interface ChatMapper extends BaseMapper<Chat> {
    @Delete("DELETE FROM chat WHERE memory_id = #{memoryId}")
    void deleteByMemoryId(String memoryId);
}
