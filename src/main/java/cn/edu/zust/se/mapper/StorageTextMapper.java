package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.StorageText;
import cn.edu.zust.se.domain.po.UserMessages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StorageTextMapper extends BaseMapper<StorageText> {
    @Select("SELECT id, memory_id, text, create_time " +
            "FROM storage_text " +
            "WHERE memory_id = #{memoryId} and id < #{lastId} " +
            "ORDER BY id DESC " +
            "LIMIT #{pageSize}")
    List<StorageText> findStorageBeforeId(@Param("lastId") Long lastId,
                                          @Param("pageSize") Integer pageSize,
                                          @Param("memoryId") String memoryId);

    @Delete("DELETE FROM storage_text WHERE memory_id = #{memoryId}")
    void deleteByMemoryId(@Param("memoryId") String memoryId);
}
