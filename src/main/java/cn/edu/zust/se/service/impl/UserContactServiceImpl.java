package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.dto.UserContactDTO;
import cn.edu.zust.se.domain.po.User;
import cn.edu.zust.se.domain.po.UserAvatar;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.query.ContactQuery;
import cn.edu.zust.se.domain.vo.UserAvatarVO;
import cn.edu.zust.se.domain.vo.UserContactVO;
import cn.edu.zust.se.domain.vo.UserVO;
import cn.edu.zust.se.mapper.UserContactMapper;
import cn.edu.zust.se.service.UserAvatarServiceI;
import cn.edu.zust.se.service.UserChatListServiceI;
import cn.edu.zust.se.service.UserContactServiceI;
import cn.edu.zust.se.service.UserServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author anymore131
 * @since 2025-04-12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserContactServiceImpl extends ServiceImpl<UserContactMapper, UserContact> implements UserContactServiceI {
    private final UserServiceI userService;
    private final UserContactMapper userContactMapper;
    private final UserAvatarServiceI userAvatarService;
    private final UserChatListServiceI userChatListService;
    private final RedisTemplate redisTemplate;

    @Override
    public UserContactVO getContact(Long id) {
        UserContact u = lambdaQuery().eq(UserContact::getId, id).one();
        if (u == null){
            throw new RuntimeException("联系人不存在");
        }
        UserContactVO userContactVO = BeanUtil.copyProperties(u, UserContactVO.class);
        UserAvatarVO usedAvatar = userAvatarService.getUsedAvatar(userContactVO.getContactId());
        userContactVO.setAvatar(usedAvatar.getAvatar());
        UserVO user = userService.getUser(userContactVO.getUserId());
        userContactVO.setUserName(user.getUserName());
        return userContactVO;
    }

    @Override
    public UserContact getContactByContactId(Long contactId) {
        Long userId = UserContext.getUser().getUserId();
        QueryWrapper<UserContact> wrapper = new QueryWrapper<>();
        wrapper.eq("contact_id", contactId)
                .eq("user_id", userId);
        return getOne(wrapper);
    }

    @Override
    public List<UserContactVO> getContacts(ContactQuery query) {
        Long userId = UserContext.getUser().getUserId();
        if (query.getStatus() == 2){
            throw new RuntimeException("不能查询已删除的联系人");
        }
        if (query.getStatus() != 1 && query.getStatus() != 3 && query.getStatus() != 4 ||
                query.getContactType() != 1 && query.getContactType() != 2){
            throw new RuntimeException("查询参数错误");
        }
        QueryWrapper<UserContact> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("status", query.getStatus())
                .eq("contact_type", query.getContactType());
        try {
            List<UserContact> userContacts = userContactMapper.selectList(wrapper);
            if (userContacts != null){
                List<UserContactVO> userContactVOS = BeanUtils.copyList(userContacts, UserContactVO.class);
                for (UserContactVO userContactVO : userContactVOS){
                    UserAvatarVO usedAvatar = userAvatarService.getUsedAvatar(userContactVO.getContactId());
                    userContactVO.setAvatar(usedAvatar.getAvatar());
                    UserVO user = userService.getUser(userContactVO.getUserId());
                    userContactVO.setUserName(user.getUserName());
                }
                return userContactVOS;
            }
        }catch (NullPointerException e){
            log.error("查询联系人失败", e);
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public List<UserContactVO> getShouldConfirmContacts() {
        Long userId = UserContext.getUser().getUserId();
        QueryWrapper<UserContact> wrapper = new QueryWrapper<>();
        wrapper.eq("contact_id", userId)
                .eq("status", 3);
        try {
            List<UserContact> userContacts = userContactMapper.selectList(wrapper);
            if (userContacts != null){
                List<UserContactVO> userContactVOS = BeanUtils.copyList(userContacts, UserContactVO.class);
                for (UserContactVO userContactVO : userContactVOS){
                    UserAvatarVO usedAvatar = userAvatarService.getUsedAvatar(userContactVO.getUserId());
                    userContactVO.setAvatar(usedAvatar.getAvatar());
                    UserVO user = userService.getUser(userContactVO.getUserId());
                    userContactVO.setUserName(user.getUserName());
                }
                return userContactVOS;
            }
        }catch (NullPointerException e){
            log.error("查询联系人失败", e);
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public void updateContact(UserContactDTO userContact) {
        Long userId = UserContext.getUser().getUserId();
        UserContact u = lambdaQuery().eq(UserContact::getId, userContact.getId()).one();
        if (u == null){
            throw new RuntimeException("联系人不存在");
        }
        if (!Objects.equals(u.getUserId(), userId)){
            throw new RuntimeException("无权限修改");
        }
        if (userContact.getStatus() != null){
            if (userContact.getStatus() == 2){
                u.setStatus(userContact.getStatus());
                redisTemplate.delete("contact:" + userId + ":" + u.getContactId());
                userChatListService.deleteChatList(u.getContactId());
            }
        }
        if (userContact.getContactName() != null){
            u.setContactName(userContact.getContactName());
        }
        u.setUpdateTime(LocalDateTime.now());
        updateById(u);
    }

    @Override
    public void addContact(UserContact userContact) {
        Long userId = UserContext.getUser().getUserId();
        if (userContact.getContactType() != 1 && userContact.getContactType() != 2){
            throw new RuntimeException("联系人类型错误");
        }
        UserContact u;
        UserContact one = lambdaQuery()
                .eq(UserContact::getUserId, userId)
                .eq(UserContact::getContactId, userContact.getContactId())
                .eq(UserContact::getContactType, userContact.getContactType())
                .one();
        if (one != null){
            if (one.getStatus() == 1){
                throw new RuntimeException("联系人已存在");
            } else {
                u = one;
                u.setStatus(3);
                u.setUpdateTime(LocalDateTime.now());
                updateById(u);
                redisTemplate.opsForValue().set("contact:" + userId + ":" + u.getUserId(), 3);
            }
        }else{
            u = new UserContact();
            u.setUserId(userId);
            u.setContactId(userContact.getContactId());
            u.setContactName(userContact.getContactName());
            u.setContactType(userContact.getContactType());
            u.setStatus(3);
            u.setCreateTime(LocalDateTime.now());
            save(u);
            redisTemplate.opsForValue().set("contact:" + userId + ":" + u.getUserId(), 1);
        }
    }

    @Override
    public void confirmContact(UserContactDTO userContact) {
        Long userId = UserContext.getUser().getUserId();
        UserContact u = lambdaQuery().eq(UserContact::getId, userContact.getId()).one();
        if (u == null){
            throw new RuntimeException("联系人不存在");
        }
        if (!Objects.equals(u.getContactId(), userId)){
            throw new RuntimeException("无权限修改");
        }
        if (userContact.getStatus() != 1 && userContact.getStatus() != 4){
            throw new RuntimeException("状态错误");
        }
        u.setStatus(userContact.getStatus());
        u.setUpdateTime(LocalDateTime.now());
        updateById(u);
        if (userContact.getStatus() == 1){
            UserContact userContact1 = new UserContact();
            userContact1.setUserId(userId);
            userContact1.setContactId(u.getUserId());
            if (userContact.getContactName()==null){
                User user = (User) redisTemplate.opsForValue().get("user:" + u.getUserId());
                if (Objects.isNull(user)){
                    user = userService.getById(userId);
                }
                userContact1.setContactName(user.getUserName());
            }else {
                userContact1.setContactName(userContact.getContactName());
            }
            userContact1.setContactType(u.getContactType());
            userContact1.setStatus(1);
            userContact1.setCreateTime(LocalDateTime.now());
            save(userContact1);
            redisTemplate.opsForValue().set("contact:" + userId + ":" + u.getUserId(), 1);
            redisTemplate.opsForValue().set("contact:" + u.getUserId() + ":" + userId, 1);
        }else {
            redisTemplate.opsForValue().set("contact:" + userId + ":" + u.getUserId(), 4);
            redisTemplate.opsForValue().set("contact:" + u.getUserId() + ":" + userId, 4);
        }
    }

    @Override
    public boolean checkContact(Long userId, Long contactId) {
        Integer i1 = (Integer) redisTemplate.opsForValue().get("contact:" + userId + ":" + contactId);
        Integer i2 = (Integer) redisTemplate.opsForValue().get("contact:" + contactId + ":" + userId);
        if (i1 != null && i2 != null){
            if (i1 == 1 && i2 == 1){
                return true;
            }else{
                return false;
            }
        }
        UserContact u1 = lambdaQuery()
                .eq(UserContact::getUserId, userId)
                .eq(UserContact::getContactId, contactId)
                .one();
        UserContact u2 = lambdaQuery()
                .eq(UserContact::getUserId, contactId)
                .eq(UserContact::getContactId, userId)
                .one();
        if (u1 == null || u2 == null){
            return false;
        }
        redisTemplate.opsForValue().set("contact:" + userId + ":" + contactId, u1.getStatus());
        redisTemplate.opsForValue().set("contact:" + contactId + ":" + userId, u2.getStatus());
        if (u1.getStatus() == 1 && u2.getStatus() == 1){
            return true;
        }
        return false;
    }
}
