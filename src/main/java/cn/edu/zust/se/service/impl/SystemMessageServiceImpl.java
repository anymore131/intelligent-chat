package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.SystemUsed;
import cn.edu.zust.se.domain.query.SystemMessageQuery;
import cn.edu.zust.se.domain.vo.SystemMessageVO;
import cn.edu.zust.se.domain.po.SystemMessage;
import cn.edu.zust.se.mapper.SystemMessageMapper;
import cn.edu.zust.se.mapper.SystemUsedMapper;
import cn.edu.zust.se.service.SystemMessageServiceI;
import cn.edu.zust.se.service.SystemUsedServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author anymore131
 * @since 2025-03-25
 */
@Service
@RequiredArgsConstructor
public class SystemMessageServiceImpl extends ServiceImpl<SystemMessageMapper, SystemMessage> implements SystemMessageServiceI {
    private final SystemUsedMapper systemUsedMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SystemMessageMapper systemMessageMapper;

    @Override
    @Transactional
    public SystemMessageVO createByUserId(Long userId,String description, String name, String accessPolicy) {
        if (!accessPolicy.equals("public") && !accessPolicy.equals("private")){
            throw new RuntimeException("访问权限错误！");
        }
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setUserId(userId);
        systemMessage.setDescription(description);
        systemMessage.setName(name);
        systemMessage.setUsedNumber(0L);
        systemMessage.setAccessPolicy(accessPolicy);
        systemMessage.setCreateTime(LocalDateTime.now());
        save(systemMessage);
        redisTemplate.opsForValue().set("daily_count:normal:system",(Integer)redisTemplate.opsForValue().get("daily_count:normal:system") + 1);
        return BeanUtil.copyProperties(systemMessage, SystemMessageVO.class);
    }

    @Override
    public List<SystemMessageVO> getByUserId() {
        Long userId = UserContext.getUser().getUserId();
        List<SystemMessage> systemMessages = lambdaQuery().eq(SystemMessage::getUserId, userId).list();
        return BeanUtil.copyToList(systemMessages, SystemMessageVO.class);
    }

    @Override
    @Transactional
    public void usedUp(Long systemId) {
        SystemMessage systemMessage = lambdaQuery().eq(SystemMessage::getId, systemId).one();
        if (systemMessage == null){
            throw new RuntimeException("对话记录不存在！");
        }
        systemMessage.setUsedNumber(systemMessage.getUsedNumber() + 1);
        updateById(systemMessage);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Long userId = UserContext.getUser().getUserId();
        SystemMessage systemMessage = lambdaQuery().eq(SystemMessage::getId, id).one();
        if (systemMessage == null){
            throw new RuntimeException("对话记录不存在！");
        }
        if (!Objects.equals(systemMessage.getUserId(), userId)){
            throw new RuntimeException("无权限删除！");
        }
        systemMessage.setAccessPolicy("delete");
        saveOrUpdate(systemMessage);
        redisTemplate.opsForValue().set("daily_count:normal:system",(Integer)redisTemplate.opsForValue().get("daily_count:normal:system") - 1);
    }

    @Override
    @Transactional
    public void change(SystemMessage systemMessage) {
        Long userId = UserContext.getUser().getUserId();
        systemMessage.setUserId(userId);
        SystemMessage systemMessage1 = lambdaQuery().eq(SystemMessage::getId, systemMessage.getId()).one();
        if (systemMessage1 == null){
            throw new RuntimeException("角色不存在！");
        }
        if (!Objects.equals(systemMessage1.getUserId(), systemMessage.getUserId())){
            throw new RuntimeException("无权限修改！");
        }
        if (systemMessage.getAccessPolicy() != null && !systemMessage.getAccessPolicy().isEmpty()){
            if (systemMessage.getAccessPolicy().equals("public") || systemMessage.getAccessPolicy().equals("private")){
                systemMessage1.setAccessPolicy(systemMessage.getAccessPolicy());
            }
        }
        if (systemMessage.getDescription() != null && !systemMessage.getDescription().isEmpty()){
            systemMessage1.setDescription(systemMessage.getDescription());
        }
        if (systemMessage.getName() != null && !systemMessage.getName().isEmpty()){
            systemMessage1.setName(systemMessage.getName());
        }
        updateById(systemMessage1);
    }

    @Override
    @Transactional
    public SystemMessage getByMemoryId(String memoryId) {
        SystemUsed systemUsed = systemUsedMapper.getByMemoryId(memoryId);
        if (systemUsed != null){
            return lambdaQuery().eq(SystemMessage::getId, systemUsed.getSystemId()).one();
        }
        return null;
    }

    @Override
    public Long countSystem() {
        return lambdaQuery().count();
    }

    @Override
    public PageDTO<SystemMessageVO> pageByQuery(SystemMessageQuery query) {
        Page<SystemMessage> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<SystemMessageVO> iPage = systemMessageMapper.pageByQuery(page,query);
        if (iPage == null){
            return new PageDTO<>(0L,0L, null);
        }
        PageDTO<SystemMessageVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }

    @Override
    public void create(SystemMessageVO systemMessageVO) {
        SystemMessage systemMessage = BeanUtils.copyBean(systemMessageVO, SystemMessage.class);
        checkKeys(systemMessage);
        systemMessage.setCreateTime(LocalDateTime.now());
        save(systemMessage);
    }

    @Override
    public void updateByVO(SystemMessageVO systemMessageVO) {
        if (systemMessageVO.getId() == null){
            throw new RuntimeException("id不能为空！");
        }
        SystemMessage systemMessage = BeanUtils.copyBean(systemMessageVO, SystemMessage.class);
        checkKeys(systemMessage);
        updateById(systemMessage);
    }

    private void checkKeys(SystemMessage systemMessage){
        if (systemMessage.getUserId() == null){
            throw new RuntimeException("用户id不能为空！");
        }
        if (systemMessage.getDescription() == null){
            throw new RuntimeException("描述不能为空！");
        }
        if (systemMessage.getName() == null){
            throw new RuntimeException("名称不能为空！");
        }
        if (systemMessage.getAccessPolicy() == null){
            throw new RuntimeException("访问权限不能为空！");
        }
        if (!systemMessage.getAccessPolicy().equals("delete") && !systemMessage.getAccessPolicy().equals("public") && !systemMessage.getAccessPolicy().equals("private")){
            throw new RuntimeException("访问权限错误！");
        }
        if (systemMessage.getUsedNumber() == null){
            systemMessage.setUsedNumber(0L);
        }
    }
}
