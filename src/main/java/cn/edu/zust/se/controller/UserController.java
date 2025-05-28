package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.LoginFormDTO;
import cn.edu.zust.se.domain.po.User;
import cn.edu.zust.se.domain.vo.UserLoginVO;
import cn.edu.zust.se.domain.vo.UserVO;
import cn.edu.zust.se.service.UserServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("user")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceI userService;

    @PostMapping("/login")
    public R<UserLoginVO> login(@RequestBody LoginFormDTO loginFormDTO) {
        UserLoginVO userLoginVO = userService.login(loginFormDTO);
        return R.ok(userLoginVO);
    }

    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        boolean register = userService.register(user);
        return register ? R.ok("注册成功") : R.error("注册失败");
    }

    @PostMapping("/logout")
    public R<String> logout() {
        userService.logout();
        return R.ok("退出成功");
    }

    @GetMapping("/{id}")
    public R<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUser(id);
        return R.ok(user);
    }

    @GetMapping("/check-admin")
    public R<Boolean> checkAdmin() {
        boolean admin = userService.checkAdmin();
        return R.ok(admin);
    }

    @GetMapping("/check")
    public R<Void> check() {;
        return R.ok();
    }
}
