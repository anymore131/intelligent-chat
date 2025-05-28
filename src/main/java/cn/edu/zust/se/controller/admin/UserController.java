package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.query.LanguageModelQuery;
import cn.edu.zust.se.domain.query.UserNameQuery;
import cn.edu.zust.se.domain.query.UserQuery;
import cn.edu.zust.se.domain.vo.AdminUserVO;
import cn.edu.zust.se.domain.vo.UserVO;
import cn.edu.zust.se.service.UserServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("admin")
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceI userService;

    @GetMapping("/getAdminUserList")
    public R<List<AdminUserVO>> getAdminUserList(){
        List<AdminUserVO> adminUserList = userService.getAdminUserList();
        return R.ok(adminUserList);
    }

    @PostMapping("/page")
    public R<PageDTO<UserVO>> page(@RequestBody UserQuery query){
        PageDTO<UserVO> page = userService.adminPage(query);
        return R.ok(page);
    }

    @PutMapping("/permissions")
    public R<String> updatePermissions(@RequestParam("userId") Long userId,
                                       @RequestParam("admin") Integer admin){
        userService.updatePermissions(userId,admin);
        return R.ok("更新成功");
    }

    @PostMapping("/pageByUserName")
    public R<PageDTO<UserVO>> pageByUserName(@RequestBody UserNameQuery query){
        PageDTO<UserVO> page = userService.pageByUserName(query);
        return R.ok(page);
    }

    @GetMapping("/admin")
    public R<Integer> getAdmin(){
        Integer admin = userService.gerAdmin();
        return R.ok(admin);
    }
}
