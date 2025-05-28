package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.po.UserChatList;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.vo.UserChatListVO;
import cn.edu.zust.se.mapper.UserChatListMapper;
import cn.edu.zust.se.service.UserChatListServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author anymore131
 * @since 2025-04-22
 */
@Service
@RequiredArgsConstructor
public class UserChatListServiceImpl extends ServiceImpl<UserChatListMapper, UserChatList> implements UserChatListServiceI {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 创建聊天对象
     * @param userContact 联系人
     * @return 聊天对象
     */
    @Override
    public UserChatList createChatList(UserContact userContact) {
        QueryWrapper<UserChatList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userContact.getUserId())
                .eq("contact_id", userContact.getContactId())
                .eq("contact_type", userContact.getContactType());
        UserChatList result = getOne(wrapper);
        if (result!=null && result.getIsDelete() == 0){
            return result;
        }
        if (result!=null){
            result.setLastMessage("");
            result.setLastMessageTime(null);
            result.setUnreadCount(0);
            result.setIsPinned(0);
            result.setUpdateTime(LocalDateTime.now());
            updateById(result);
            redisTemplate.opsForSet().add("chatList:" + userContact.getUserId() , userContact.getContactId());
            return result;
        }
        UserChatList userChatList = new UserChatList();
        userChatList.setUserId(userContact.getUserId());
        userChatList.setContactId(userContact.getContactId());
        userChatList.setContactType(userContact.getContactType());
        userChatList.setName(userContact.getContactName());
        userChatList.setLastMessage("");
        userChatList.setLastMessageTime(null);
        userChatList.setUnreadCount(0);
        userChatList.setIsPinned(0);
        userChatList.setIsDelete(0);
        userChatList.setCreateTime(LocalDateTime.now());
        userChatList.setUpdateTime(null);
        save(userChatList);
        redisTemplate.opsForSet().add("chatList:" + userContact.getUserId() , userContact.getContactId());
        return userChatList;
    }

    /**
     * 更新聊天对象信息
     * @param contactId 联系人id
     * @param message 消息
     */
    @Override
    public void updateLastMessage(Long userId, Long contactId, String message) {
        UserChatList userChatList = lambdaQuery()
                .eq(UserChatList::getUserId, userId)
                .eq(UserChatList::getContactId, contactId)
                .eq(UserChatList::getIsDelete, 0)
                .one();
        userChatList.setLastMessage(message);
        userChatList.setLastMessageTime(LocalDateTime.now());
        userChatList.setUnreadCount(userChatList.getUnreadCount()+1);
        updateById(userChatList);
    }

    /**
     * 置顶聊天对象
     * @param contactId 联系人id
     * @param pinned 置顶状态
     */
    @Override
    public void updatePinned(Long contactId, Integer pinned) {
        Long userId = UserContext.getUser().getUserId();
        if (pinned == null){
            throw new RuntimeException("置顶错误01！");
        }
        if (pinned != 0 && pinned != 1){
            throw new RuntimeException("置顶错误02！");
        }
        UserChatList userChatList = lambdaQuery()
                .eq(UserChatList::getUserId, userId)
                .eq(UserChatList::getContactId, contactId)
                .one();
        if (userChatList == null){
            throw new RuntimeException("对话记录不存在！");
        }
        userChatList.setIsPinned(pinned);
        updateById(userChatList);
    }

    /**
     * 清除未读消息
     * @param contactId 联系人id
     */
    @Override
    public void clearUnreadCount(Long userId, Long contactId) {
        boolean update = lambdaUpdate()
                .eq(UserChatList::getUserId, userId)
                .eq(UserChatList::getContactId, contactId)
                .set(UserChatList::getUnreadCount, 0)
                .update();
        if (!update){
            throw new RuntimeException("清除错误！");
        }
    }

    /**
     * 获取聊天对象列表
     * @return 聊天列表
     */
    @Override
    public List<UserChatListVO> getChatList() {
        Long userId = UserContext.getUser().getUserId();
        QueryWrapper<UserChatList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_delete", 0)
                .orderByDesc("is_pinned")
                .orderByDesc("last_message_time")
                .orderByDesc("update_time")
                .orderByDesc("create_time");
        List<UserChatList> userChatLists = list(wrapper);
        if (userChatLists != null && !userChatLists.isEmpty()){
            Set<Long> set = userChatLists.stream().map(UserChatList::getContactId).collect(Collectors.toSet());
            redisTemplate.delete("chatList:" + userId);
            set.forEach(id -> redisTemplate.opsForSet().add("chatList:" + userId, id));
            return BeanUtils.copyList(userChatLists, UserChatListVO.class);
        }else {
            return Collections.emptyList();
        }
    }

    /**
     * 删除聊天对象
     * @param contactId 联系人id
     */
    @Override
    public void deleteChatList(Long contactId) {
        Long userId = UserContext.getUser().getUserId();
        boolean b = redisTemplate.opsForSet().isMember("chatList:" + userId, contactId);
        if (!b){
            throw new RuntimeException("聊天对象不存在！");
        }
        boolean update = lambdaUpdate().eq(UserChatList::getUserId, userId)
                .eq(UserChatList::getContactId, contactId)
                .set(UserChatList::getIsDelete, 1)
                .update();
        if (!update){
            throw new RuntimeException("聊天对象删除错误！");
        }
        redisTemplate.opsForSet().remove("chatList:" + userId, contactId);
    }

    /**
     * 获取聊天对象
     * @param userContact 联系人
     * @return 聊天对象
     */
    @Override
    public UserChatList getChat(UserContact userContact) {
        Long userId = UserContext.getUser().getUserId();
        UserChatList chat;
        boolean b = redisTemplate.opsForSet().isMember("chatList:" + userId, userContact.getContactId());
        if (!b){
            chat = createChatList(userContact);
            return chat;
        }
        QueryWrapper<UserChatList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("contact_id",userContact.getContactId())
                .eq("is_delete", 0);
        chat = getOne(wrapper);
        return chat;
    }

    @Override
    public UserChatList getUserChatList(Long userId, Long contactId) {
        QueryWrapper<UserChatList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("contact_id", contactId)
                .eq("is_delete", 0);
        return getOne(wrapper);
    }
}
