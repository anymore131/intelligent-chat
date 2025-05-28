package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.UserRag;
import cn.edu.zust.se.domain.query.UserRagQuery;
import cn.edu.zust.se.domain.vo.UserRagVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author anymore131
 * @since 2025-03-23
 */
public interface UserRagMapper extends BaseMapper<UserRag> {
    IPage<UserRagVO> pageByQuery(@Param("query") UserRagQuery query,
                                 @Param("page") Page<UserRagVO> page);
}
