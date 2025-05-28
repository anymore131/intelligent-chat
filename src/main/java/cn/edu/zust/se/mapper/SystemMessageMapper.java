package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.SystemMessage;
import cn.edu.zust.se.domain.query.SystemMessageQuery;
import cn.edu.zust.se.domain.vo.SystemMessageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author anymore131
 * @since 2025-03-25
 */
public interface SystemMessageMapper extends BaseMapper<SystemMessage> {
    IPage<SystemMessageVO> pageByQuery(@Param("page") Page<SystemMessage> page,
                                       @Param("query") SystemMessageQuery query);
}
