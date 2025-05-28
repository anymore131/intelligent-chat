package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.UserAvatar;
import cn.edu.zust.se.domain.query.AvatarFileQuery;
import cn.edu.zust.se.domain.vo.AvatarFileVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author anymore131
 * @since 2025-04-11
 */
public interface UserAvatarMapper extends BaseMapper<UserAvatar> {
    IPage<AvatarFileVO> adminPageByQuery(@Param("page") Page<AvatarFileVO> page,
                                         @Param("query") AvatarFileQuery query);
}
