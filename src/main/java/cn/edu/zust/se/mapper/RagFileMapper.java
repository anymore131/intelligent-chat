package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.RagFile;
import cn.edu.zust.se.domain.query.RagFileQuery;
import cn.edu.zust.se.domain.vo.RagFileVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author anymore131
 * @since 2025-03-23
 */
public interface RagFileMapper extends BaseMapper<RagFile> {
    IPage<RagFileVO> adminPageByQuery(@Param("page") Page<RagFileVO> page,
                                      @Param("query") RagFileQuery query);
}
