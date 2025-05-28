package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.po.UserChatList;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.vo.UserChatListVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-22
 */
public interface UserChatListServiceI extends IService<UserChatList> {
    UserChatList createChatList(UserContact userContact);
    void updateLastMessage(Long userId, Long contactId, String message);
    void updatePinned(Long contactId, Integer pinned);
    void clearUnreadCount(Long userId, Long contactId);
    List<UserChatListVO> getChatList();
    void deleteChatList(Long contactId);
    UserChatList getChat(UserContact userContact);
    UserChatList getUserChatList(Long userId, Long contactId);
}