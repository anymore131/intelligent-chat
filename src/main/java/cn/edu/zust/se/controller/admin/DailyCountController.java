package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.service.CountServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("adminBasic")
@RequestMapping("/admin/daily-count")
@RequiredArgsConstructor
public class DailyCountController {
    private final CountServiceI countServiceI;

    @GetMapping("/init/normal")
    public R<Map<String, Object>> initNormal() {
        Map<String, Object> normal = countServiceI.init("normal");
        return R.ok(normal);
    }

    @GetMapping("/init/chat")
    public R<Map<String, Object>> initChat() {
        Map<String, Object> chat = countServiceI.init("chat");
        return R.ok(chat);
    }

    @PostMapping("/text")
    public R<String> updateDailyCount() {
        countServiceI.updateDailyCount();
        return R.ok("更新成功");
    }
}
