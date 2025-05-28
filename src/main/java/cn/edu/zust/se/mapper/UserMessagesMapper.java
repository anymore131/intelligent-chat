package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.query.MessageQuery;
import cn.edu.zust.se.domain.query.TotalMessageQuery;
import cn.edu.zust.se.domain.vo.UserMessagesVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface UserMessagesMapper extends BaseMapper<UserMessages> {
    IPage<UserMessages> pageByQuery(@Param("page") Page<UserMessages> page,
                                    @Param("query") MessageQuery query);

    IPage<UserMessagesVO> pageByTotalQuery(@Param("page")Page<UserMessages> page,
                                           @Param("query")TotalMessageQuery query);
}
