package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.UserMessagesFile;
import cn.edu.zust.se.domain.query.MessageFileQuery;
import cn.edu.zust.se.domain.vo.MessageFileVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author anymore131
 * @since 2025-05-13
 */
public interface UserMessagesFileMapper extends BaseMapper<UserMessagesFile> {

    IPage<MessageFileVO> adminPageByQuery(@Param("page") Page<MessageFileVO> page,
                                          @Param("query") MessageFileQuery query);
}
