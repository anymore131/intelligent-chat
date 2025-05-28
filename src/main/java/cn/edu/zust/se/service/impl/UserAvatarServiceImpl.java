package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.UserAvatar;
import cn.edu.zust.se.domain.query.AvatarFileQuery;
import cn.edu.zust.se.domain.vo.AvatarFileVO;
import cn.edu.zust.se.domain.vo.UserAvatarVO;
import cn.edu.zust.se.mapper.UserAvatarMapper;
import cn.edu.zust.se.service.UserAvatarServiceI;
import cn.edu.zust.se.util.MinioUtil;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar> implements UserAvatarServiceI {
    private final MinioUtil minioUtil;
    private final UserAvatarMapper userAvatarMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public UserAvatarVO getUsedAvatar(Long userId) {
        UserAvatar userAvatar = null;
        boolean b = redisTemplate.hasKey("avatar:" + userId);
        if (b){
            userAvatar = (UserAvatar) redisTemplate.opsForValue().get("avatar:" + userId);
            return BeanUtil.copyProperties(userAvatar, UserAvatarVO.class);
        }
        userAvatar = lambdaQuery().eq(UserAvatar::getUserId, userId).eq(UserAvatar::getStatus, 1).one();
        if (userAvatar != null){
            redisTemplate.opsForValue().set("avatar:" + userId, userAvatar, 60 * 60, TimeUnit.SECONDS);
            return BeanUtil.copyProperties(userAvatar, UserAvatarVO.class);
        }
        return null;
    }

    @Override
    @Transactional
    public List<UserAvatarVO> getAvatarList(Long userId) {
        List<UserAvatar> userAvatars = lambdaQuery().eq(UserAvatar::getUserId, userId).list();
        if (userAvatars != null && !userAvatars.isEmpty()){
            return BeanUtil.copyToList(userAvatars, UserAvatarVO.class);
        }
        return List.of();
    }

    @Override
    public void updateAvatar(Long userId,MultipartFile file) {
        if (file == null){
            throw new RuntimeException("文件为空！");
        }
        String uuidFileName = minioUtil.upload(file);
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setUserId(userId);
        userAvatar.setAvatar(minioUtil.preview(uuidFileName, file.getOriginalFilename()));
        userAvatar.setStatus(0);
        userAvatar.setCreateTime(LocalDateTime.now());
        save(userAvatar);
    }

    @Override
    @Transactional
    public void changeUsedAvatar(Long id) {
        Long userId = UserContext.getUser().getUserId();
        UserAvatar userUsedAvatar = lambdaQuery().eq(UserAvatar::getUserId, userId).eq(UserAvatar::getStatus, 1).one();
        UserAvatar userAvatar = getById(id);
        if(!Objects.equals(userAvatar.getUserId(), userId)){
            throw new RuntimeException("无权限修改！");
        }
        userAvatar.setStatus(1);
        userUsedAvatar.setStatus(0);
        userAvatar.setUpdateTime(LocalDateTime.now());
        userUsedAvatar.setUpdateTime(LocalDateTime.now());
        updateBatchById(Arrays.asList(userUsedAvatar, userAvatar));
        redisTemplate.opsForValue().set("avatar:" + userId, userAvatar);
    }

    @Override
    public void initAvatar(Long userId) {
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setUserId(userId);
        userAvatar.setAvatar("http://47.99.150.135:9000/ai-test/%E9%BB%98%E8%AE%A4.jpg");
        userAvatar.setStatus(1);
        userAvatar.setCreateTime(LocalDateTime.now());
        save(userAvatar);
        redisTemplate.opsForValue().set("avatar:" + userId, userAvatar);
    }

    @Override
    public PageDTO<AvatarFileVO> adminPageByQuery(AvatarFileQuery query) {
        Page<AvatarFileVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<AvatarFileVO> iPage = userAvatarMapper.adminPageByQuery(page, query);
        if (iPage == null){
            return PageDTO.empty(0L,0L);
        }
        PageDTO<AvatarFileVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }
}
