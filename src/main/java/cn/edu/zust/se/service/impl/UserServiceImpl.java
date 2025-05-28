package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.LoginFormDTO;
import cn.edu.zust.se.domain.po.User;
import cn.edu.zust.se.domain.query.UserNameQuery;
import cn.edu.zust.se.domain.query.UserQuery;
import cn.edu.zust.se.domain.vo.AdminUserVO;
import cn.edu.zust.se.domain.vo.LanguageVO;
import cn.edu.zust.se.domain.vo.UserLoginVO;
import cn.edu.zust.se.domain.vo.UserVO;
import cn.edu.zust.se.mapper.UserMapper;
import cn.edu.zust.se.service.UserAvatarServiceI;
import cn.edu.zust.se.service.UserServiceI;
import cn.edu.zust.se.util.JwtUtil;
import cn.edu.zust.se.util.PasswordUtil;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserServiceI {
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserAvatarServiceI userAvatarService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserLoginVO login(LoginFormDTO loginFormDTO) {
        String userName = loginFormDTO.getUserName();
        String password = loginFormDTO.getPassword();
        User user = null;
        boolean b = redisTemplate.hasKey("user:" + userName);
        if (b){
            user = (User) redisTemplate.opsForValue().get("user:" + userName);
        }else{
            user = lambdaQuery().eq(User::getUserName, userName).one();
        }
        if (Objects.isNull(user)){
            throw new RuntimeException("用户名错误！");
        }
        if (!passwordUtil.matches(password,user.getPassword())){
            throw new RuntimeException("密码错误！");
        }
        if (loginFormDTO.isAdmin() && user.getAdmin() == 0){
            throw new RuntimeException("您不是管理员！");
        }
        String token = jwtUtil.generateToken(user.getId(),userName,user.getAdmin());
        redisTemplate.opsForValue().set("user:"+userName,user,30,TimeUnit.MINUTES);
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setId(user.getId());
        userLoginVO.setToken(token);
        userLoginVO.setUserName(user.getUserName());
        userLoginVO.setName(user.getName());
        userLoginVO.setAdmin(user.getAdmin() == 1 || user.getAdmin() == 2);
        return userLoginVO;
    }

    @Override
    @Transactional
    public boolean register(User user) {
        User user1 = null;
        user.setAdmin(0);
        boolean b = redisTemplate.hasKey("user:" + user.getUserName());
        if (b){
            user1 = (User) redisTemplate.opsForValue().get("user:" + user.getUserName());
        } else{
            user1 = lambdaQuery().eq(User::getUserName, user.getUserName()).one();
        }
        Assert.isNull(user1, "用户名已存在");
        user.setPassword(passwordUtil.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        if (save(user)){
            userAvatarService.initAvatar(user.getId());
            redisTemplate.opsForValue().set("daily_count:normal:user",(Integer)redisTemplate.opsForValue().get("daily_count:normal:user") + 1);
            redisTemplate.opsForValue().set("user:"+user.getUserName(),user,30,TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    @Override
    public void logout() {
        UserContext.removeUser();
    }

    @Override
    public UserVO getUser(Long id) {
        User user = getById(id);
        return BeanUtil.copyProperties(user,UserVO.class);
    }

    @Override
    public boolean checkAdmin() {
        return UserContext.getUser().getAdmin() != 0;
    }

    @Override
    public List<AdminUserVO> getAdminUserList() {
        // todo admin检验
        List<User> users = lambdaQuery()
                .eq(User::getAdmin, 1).or().eq(User::getAdmin, 2)
                .list();
        return BeanUtil.copyToList(users, AdminUserVO.class);
    }

    @Override
    @Transactional
    public PageDTO<UserVO> adminPage(UserQuery query) {
        // todo admin检验
        Page<UserVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<UserVO> iPage = userMapper.pageByQuery(page, query);
        if (iPage == null){
            return new PageDTO<>(0L,0L, null);
        }
        PageDTO<UserVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }

    @Override
    @Transactional
    public void updatePermissions(Long userId, Integer admin) {
        Integer a = UserContext.getUser().getAdmin();
        if (a != 2){
            throw new RuntimeException("权限不足！");
        }
        if (admin != 0 && admin != 1){
            throw new RuntimeException("设置权限错误！");
        }
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", userId).set("admin", admin);
        if (update(wrapper)){
            User user = getById(userId);
            redisTemplate.opsForValue().set("user:"+user.getUserName(),user,30,TimeUnit.MINUTES);
            if (admin == 1){
                redisTemplate.opsForValue().set("daily_count:normal:admin",(Integer)redisTemplate.opsForValue().get("daily_count:normal:admin") + 1);
            }else {
                redisTemplate.opsForValue().set("daily_count:normal:admin",(Integer)redisTemplate.opsForValue().get("daily_count:normal:admin") - 1);
            }
        }else {
            throw new RuntimeException("更新失败！");
        }
    }

    @Override
    public Long countUser() {
        return lambdaQuery().eq(User::getAdmin, 0).count();
    }

    @Override
    public Long countAdmin() {
        return lambdaQuery().eq(User::getAdmin, 1).count();
    }

    @Override
    public PageDTO<UserVO> pageByUserName(UserNameQuery query) {
        Page<UserVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<UserVO> iPage = userMapper.pageByUserName(page, query);
        if (iPage == null){
            return new PageDTO<>(0L,0L, null);
        }
        PageDTO<UserVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }

    @Override
    public Integer gerAdmin() {
        return UserContext.getUser().getAdmin();
    }
}
