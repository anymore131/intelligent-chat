package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.vo.UserAvatarVO;
import cn.edu.zust.se.service.UserAvatarServiceI;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user-avatar")
@RequiredArgsConstructor
public class UserAvatarController {
    private final UserAvatarServiceI userAvatarService;

    @GetMapping()
    public R<UserAvatarVO> getUsedAvatar() {
        Long userId = UserContext.getUser().getUserId();
        UserAvatarVO usedAvatar = userAvatarService.getUsedAvatar(userId);
        return R.ok(usedAvatar);
    }

    @GetMapping("/list")
    public R<List<UserAvatarVO>> getAvatarList() {
        Long userId = UserContext.getUser().getUserId();
        List<UserAvatarVO> avatarList = userAvatarService.getAvatarList(userId);
        return R.ok(avatarList);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> updateAvatar(@RequestPart("file") MultipartFile file) {
        Long userId = UserContext.getUser().getUserId();
        userAvatarService.updateAvatar(userId,file);
        return R.ok("上传成功");
    }

    @PutMapping()
    public R<String> changeUsedAvatar(@RequestParam("id") Long id) {
        userAvatarService.changeUsedAvatar(id);
        return R.ok("修改成功");
    }
}
