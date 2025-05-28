package cn.edu.zust.se.controller;


import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.po.UserChatList;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.vo.UserChatListVO;
import cn.edu.zust.se.domain.vo.UserContactVO;
import cn.edu.zust.se.service.UserChatListServiceI;
import cn.edu.zust.se.service.UserContactServiceI;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-22
 */
@RestController
@RequestMapping("/user-chat-list")
@RequiredArgsConstructor
public class UserChatListController {
    private final UserContactServiceI userContactService;
    private final UserChatListServiceI userChatListService;

    @PostMapping("/create")
    public R<String> createChatList(@RequestParam("contactId") Long contactId) {
        UserContact contact = userContactService.getContactByContactId(contactId);
        userChatListService.createChatList(contact);
        return R.ok("创建聊天对象成功");
    }

    @GetMapping("/getChatList")
    public R<List<UserChatListVO>> getChatList() {
        List<UserChatListVO> chatList = userChatListService.getChatList();
        return R.ok(chatList);
    }

    @PutMapping("/click")
    public R<String> click(@RequestParam("contactId") Long contactId) {
        Long userId = UserContext.getUser().getUserId();
        userChatListService.clearUnreadCount(userId, contactId);
        return R.ok("点击成功");
    }

    @PutMapping("/pinned")
    public R<String> pinned(@RequestParam("contactId")Long contactId,
                            @RequestParam("pinned")Integer pinned) {
        userChatListService.updatePinned(contactId, pinned);
        return R.ok("置顶成功");
    }

    @DeleteMapping()
    public R<String> deleteChatList(@RequestParam("contactId")Long contactId) {
        userChatListService.deleteChatList(contactId);
        return R.ok("删除成功");
    }
}
