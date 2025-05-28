package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.vo.SystemUsedVO;
import cn.edu.zust.se.domain.po.SystemMessage;
import cn.edu.zust.se.domain.po.SystemUsed;
import cn.edu.zust.se.domain.po.Memory;
import cn.edu.zust.se.mapper.SystemUsedMapper;
import cn.edu.zust.se.service.MemoryServiceI;
import cn.edu.zust.se.service.SystemMessageServiceI;
import cn.edu.zust.se.service.SystemUsedServiceI;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-26
 */
@Service
@RequiredArgsConstructor
public class SystemUsedServiceImpl extends ServiceImpl<SystemUsedMapper, SystemUsed> implements SystemUsedServiceI {
    private final MemoryServiceI memoryService;
    private final SystemMessageServiceI systemMessageService;

    @Override
    @Transactional
    public SystemUsedVO create(Long systemId) {
        Long userId = UserContext.getUser().getUserId();
        SystemMessage systemMessage = systemMessageService.getById(systemId);
        if (!userId.equals(systemMessage.getUserId()) && !systemMessage.getAccessPolicy().equals("public")){
            throw new RuntimeException("该角色未公开！");
        }
        String uuid = UUID.randomUUID().toString();
        SystemUsed systemUsed = new SystemUsed();
        systemUsed.setSystemId(systemId);
        systemUsed.setUserId(userId);
        systemUsed.setMemoryId(uuid);
        systemUsed.setCreateTime(LocalDateTime.now());
        save(systemUsed);
        systemMessageService.usedUp(systemId);
        memoryService.input(uuid, userId, systemMessage.getName());
        return BeanUtil.copyProperties(systemUsed, SystemUsedVO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SystemUsed systemUsed = lambdaQuery().eq(SystemUsed::getId, id).one();
        memoryService.lambdaUpdate().eq(Memory::getMemoryId, systemUsed.getMemoryId()).remove();
        removeById(id);
    }

    @Override
    public List<SystemUsedVO> getByUserId() {
        Long userId = UserContext.getUser().getUserId();
        List<SystemUsed> systemUsed = lambdaQuery().eq(SystemUsed::getUserId, userId).list();
        return BeanUtil.copyToList(systemUsed, SystemUsedVO.class);
    }
}
