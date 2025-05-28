package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.po.User;
import cn.edu.zust.se.domain.query.UserNameQuery;
import cn.edu.zust.se.domain.query.UserQuery;
import cn.edu.zust.se.domain.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {
    IPage<UserVO> pageByQuery(@Param("page") Page<UserVO> page,
                              @Param("query") UserQuery query);
    IPage<UserVO> pageByUserName(@Param("page") Page<UserVO> page,
                                 @Param("query") UserNameQuery query);
}
