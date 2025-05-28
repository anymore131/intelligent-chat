package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.LoginFormDTO;
import cn.edu.zust.se.domain.po.User;
import cn.edu.zust.se.domain.query.UserNameQuery;
import cn.edu.zust.se.domain.query.UserQuery;
import cn.edu.zust.se.domain.vo.AdminUserVO;
import cn.edu.zust.se.domain.vo.UserLoginVO;
import cn.edu.zust.se.domain.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserServiceI extends IService<User> {
    UserLoginVO login(LoginFormDTO loginFormDTO);
    boolean register(User user);
    void logout();
    UserVO getUser(Long id);
    boolean checkAdmin();

    List<AdminUserVO> getAdminUserList();
    PageDTO<UserVO> adminPage(UserQuery query);
    void updatePermissions(Long userId, Integer admin);
    Long countUser();
    Long countAdmin();
    PageDTO<UserVO> pageByUserName(UserNameQuery query);
    Integer gerAdmin();
}
