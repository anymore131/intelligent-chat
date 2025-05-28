package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.query.MessageQuery;
import cn.edu.zust.se.domain.vo.UserMessagesVO;
import cn.edu.zust.se.service.UserMessagesServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userMessages")
@RequestMapping("/user-messages")
@RequiredArgsConstructor
public class UserMessagesController {
    private final UserMessagesServiceI userMessagesService;

    @PostMapping()
    public R<List<UserMessagesVO>> getHistoryMessages(@RequestBody MessageQuery query) {
        List<UserMessagesVO> historyMessages = userMessagesService.getHistoryMessages(query);
        return R.ok(historyMessages);
    }
}
