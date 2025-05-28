package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.UserMessagesFileDTO;
import cn.edu.zust.se.domain.po.UserChatList;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.query.MessageFileQuery;
import cn.edu.zust.se.domain.query.MessageQuery;
import cn.edu.zust.se.domain.query.TotalMessageQuery;
import cn.edu.zust.se.domain.vo.MessageFileVO;
import cn.edu.zust.se.domain.vo.UserMessagesVO;
import cn.edu.zust.se.handler.MyWebSocketHandler;
import cn.edu.zust.se.mapper.UserMessagesMapper;
import cn.edu.zust.se.service.UserChatListServiceI;
import cn.edu.zust.se.service.UserMessagesFileServiceI;
import cn.edu.zust.se.service.UserMessagesServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserMessagesServiceImpl extends ServiceImpl<UserMessagesMapper, UserMessages> implements UserMessagesServiceI {
    private final UserMessagesMapper userMessagesMapper;
    private final UserChatListServiceI userChatListService;
    private final UserMessagesFileServiceI userMessagesFileService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 持久化发送的消息，并且更新聊天对象的最后消息，用户对用户
     * @param senderId 发送者id
     * @param receiverId 接受者id
     * @param content 消息内容
     * @param contentType 消息类型
     */
    @Override
    @Transactional
    public void sendMessage(Long senderId, Long receiverId, String content, Integer contentType) {
        UserMessages userMessages = new UserMessages();
        userMessages.setSenderId(senderId);
        userMessages.setReceiverId(receiverId);
        userMessages.setContent(content);
        if (contentType == null){
            contentType = 1;
        }
        userMessages.setContentType(contentType);
        userMessages.setCreateTime(LocalDateTime.now());
        save(userMessages);
        updateLastMessage(userMessages, content);
    }

    @Override
    public void sendFileMessage(UserMessages userMessages, String fileName) {
        save(userMessages);
        String content = "[文件] " + fileName;
        updateLastMessage(userMessages, content);
        MyWebSocketHandler.sendUserMessage(userMessages.getSenderId(), userMessages.getReceiverId(), userMessages.getContent(), false, userMessages.getContentType());
    }

    @Override
    public void updateMessages(UserMessages userMessages) {
        if (userMessages.getId() == null){
            throw new RuntimeException("参数错误");
        }
        UpdateWrapper<UserMessages> updateWrapper = new UpdateWrapper<>();
        switch (userMessages.getContentType()){
            case 1:
                // 文本消息
                updateWrapper.eq("id", userMessages.getId());
                break;
            case 2:
            case 3:
            case 4:
                // 文件消息
                updateWrapper.eq("id", userMessages.getId());
                break;
            case 91:
                // 文本消息删除
                updateWrapper.eq("id", userMessages.getId());
                break;
            case 92:
                // 文件消息删除
                updateWrapper.eq("id", userMessages.getId());
                break;
            default:
                throw new RuntimeException("参数错误");
        }
        update(userMessages, updateWrapper);
    }

    @Override
    public void deleteById(Long id) {
        UserMessages userMessages = lambdaQuery().eq(UserMessages::getId, id).one();
        UpdateWrapper<UserMessages> updateWrapper = new UpdateWrapper<>();
        if (userMessages == null){
            throw new RuntimeException("消息不存在！");
        }
        switch (userMessages.getContentType()){
            case 1:
                // 删除文本消息
                userMessages.setContentType(91);
                updateWrapper.eq("id", userMessages.getId());
                break;
            case 2:
            case 3:
            case 4:
                // 删除文件消息
                userMessages.setContentType(92);
                updateWrapper.eq("id", userMessages.getId());
                break;
        }
        update(userMessages, updateWrapper);
    }

    @Override
    public void createMessages(UserMessages userMessages) {
        if (userMessages.getSenderId() == null || userMessages.getReceiverId() == null){
            throw new RuntimeException("参数错误");
        }
        if (userMessages.getContentType() != 1){
            throw new RuntimeException("只可添加文本消息");
        }
        if (userMessages.getCreateTime() == null){
            userMessages.setCreateTime(LocalDateTime.now());
        }
        save(userMessages);
    }

    @Override
    public void createWithFileMessages(UserMessages userMessages, MultipartFile file) {
        if (userMessages.getSenderId() == null || userMessages.getReceiverId() == null){
            throw new RuntimeException("参数错误");
        }
        switch(userMessages.getContentType()){
            case 2:
            case 3:
            case 4:
                break;
            default:
                throw new RuntimeException("只可添加文件消息");
        }
        if (userMessages.getCreateTime() == null){
            userMessages.setCreateTime(LocalDateTime.now());
        }
        UserMessagesFileDTO userMessagesFile = userMessagesFileService.setFile(file, userMessages.getSenderId());
        userMessages.setContent(userMessagesFile.toJson());
        save(userMessages);
    }

    /**
     * 获取历史消息
     * @param query 查询条件
     * @return 历史消息
     */
    @Override
    public List<UserMessagesVO> getHistoryMessages(MessageQuery query) {
        if (query.getReceiverId() == null || query.getSenderId() == null){
            throw new RuntimeException("参数错误");
        }
        Page<UserMessages> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<UserMessages> iPage = userMessagesMapper.pageByQuery(page, query);
        if (iPage == null){
            return List.of();
        }
        return BeanUtils.copyList(iPage.getRecords(), UserMessagesVO.class);
    }

    /**
     * 获取历史消息
     * @param number 数量
     * @return 历史消息
     */
    @Override
    public String getHistoryString(Integer number, Long receiverId) {
        StringBuilder message = new StringBuilder();
        Long senderId = UserContext.getUser().getUserId();
        MessageQuery query = new MessageQuery();
        query.setPageSize(number);
        query.setSortBy("create_time");
        query.setSenderId(senderId);
        query.setReceiverId(receiverId);
        List<UserMessagesVO> historyMessages = getHistoryMessages(query);
        for (UserMessagesVO userMessagesVO : historyMessages){
            if (userMessagesVO.getContentType() != 1){
                continue;
            }
            if (Objects.equals(senderId, userMessagesVO.getSenderId())){
                message.append("我：").append(userMessagesVO.getContent()).append("\n");
            }else{
                message.append("对方：").append(userMessagesVO.getContent()).append("\n");
            }
        }
        return message.toString();
    }

    @Override
    public PageDTO<UserMessagesVO> pageByQuery(TotalMessageQuery query) {
        Page<UserMessages> page = new Page<>(query.getPageNo(), query.getPageSize());
        if (query.getContent() != null){
            Integer[] contentType = new Integer[]{1};
            query.setContentType(contentType);
        }
        IPage<UserMessagesVO> iPage = userMessagesMapper.pageByTotalQuery(page, query);
        if (iPage.getRecords() == null){
            return PageDTO.empty(iPage.getTotal(), iPage.getPages());
        }
        List<UserMessagesVO> records = iPage.getRecords();
        PageDTO<UserMessagesVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(records);
        return pageDTO;
    }

    private void updateLastMessage(UserMessages userMessages, String content){
        // 暂无群聊方法
        Boolean b1 = redisTemplate.opsForSet().isMember("chatList:" + userMessages.getSenderId(), userMessages.getReceiverId());
        if (!b1){
            userChatListService.createChatList(new UserContact().setUserId(userMessages.getSenderId()).setContactId(userMessages.getReceiverId()).setContactType(1));
        }
        Boolean b2 = redisTemplate.opsForSet().isMember("chatList:" + userMessages.getReceiverId(), userMessages.getSenderId());
        if (!b2){
            userChatListService.createChatList(new UserContact().setUserId(userMessages.getReceiverId()).setContactId(userMessages.getSenderId()).setContactType(1));
        }
        UserChatList userChatList = userChatListService.getUserChatList(userMessages.getSenderId(), userMessages.getReceiverId());
        if (userMessages.getCreateTime().isAfter(userChatList.getLastMessageTime())){
            userChatListService.updateLastMessage(userMessages.getSenderId(), userMessages.getReceiverId(), content);
            userChatListService.updateLastMessage(userMessages.getReceiverId(), userMessages.getSenderId(), content);
            userChatListService.clearUnreadCount(userMessages.getSenderId(), userMessages.getReceiverId());
        }
    }
}
