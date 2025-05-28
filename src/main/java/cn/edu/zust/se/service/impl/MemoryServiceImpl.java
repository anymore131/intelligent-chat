package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.dto.MemoryDTO;
import cn.edu.zust.se.domain.po.Memory;
import cn.edu.zust.se.domain.po.SystemUsed;
import cn.edu.zust.se.mapper.ChatMapper;
import cn.edu.zust.se.mapper.MemoryMapper;
import cn.edu.zust.se.mapper.StorageTextMapper;
import cn.edu.zust.se.mapper.SystemUsedMapper;
import cn.edu.zust.se.service.MemoryServiceI;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl extends ServiceImpl<MemoryMapper, Memory> implements MemoryServiceI {
    private final SystemUsedMapper systemUsedMapper;
    private final ChatMapper chatMapper;
    private final StorageTextMapper storageTextMapper;

    @Override
    public void input(String memoryId, Long userId, String name) {
        Memory memory = new Memory();
        memory.setMemoryId(memoryId);
        memory.setUserId(userId);
        memory.setName(name);
        memory.setCreateTime(LocalDateTime.now());
        save(memory);
    }

    @Override
    public MemoryDTO createByUserId() {
        Long userId = UserContext.getUser().getUserId();
        String uuid = UUID.randomUUID().toString();
        Memory memory = new Memory();
        memory.setMemoryId(uuid);
        memory.setUserId(userId);
        memory.setCreateTime(LocalDateTime.now());
        save(memory);
        return BeanUtil.copyProperties(memory, MemoryDTO.class);
    }

    @Override
    public void initMemory(String memoryId,String message) {
        Memory memory = lambdaQuery().eq(Memory::getMemoryId, memoryId).one();
        if (memory == null){
            throw new RuntimeException("对话记录不存在！");
        }
        if (memory.getName() == null){
            String name = (message.length() > 10) ? message.substring(0, 10) : message;
            memory.setName(name);
            updateById(memory);
        }
    }

    @Override
    public void changeName(String memoryId, String name) {
        Memory memory = lambdaQuery().eq(Memory::getMemoryId, memoryId).one();
        if (memory == null){
            throw new RuntimeException("对话记录不存在！");
        }
        Long userId = UserContext.getUser().getUserId();
        if (!memory.getUserId().equals(userId)){
            throw new RuntimeException("对话记录不属于该用户！");
        }
        memory.setName(name);
        updateById(memory);
    }

    @Override
    public MemoryDTO getMemoryById(Long id) {
        Memory memory = getById(id);
        if (memory != null){
            MemoryDTO memoryDTO = BeanUtil.copyProperties(memory, MemoryDTO.class);
            SystemUsed systemUsed = systemUsedMapper.getByMemoryId(memoryDTO.getMemoryId());
            memoryDTO.setSystem(systemUsed != null);
            return memoryDTO;
        }
        return null;
    }

    @Override
    public Memory getMemoryByMemoryId(String memoryId) {
        return lambdaQuery().eq(Memory::getMemoryId, memoryId).one();
    }

    @Override
    public List<MemoryDTO> getMemoryIdsByUserId() {
        Long userId = UserContext.getUser().getUserId();
        List<Memory> list = lambdaQuery().eq(Memory::getUserId, userId).list();
        List<MemoryDTO> memoryDTOs = BeanUtil.copyToList(list, MemoryDTO.class);
        for (MemoryDTO memoryDTO : memoryDTOs){
            SystemUsed systemUsed = systemUsedMapper.getByMemoryId(memoryDTO.getMemoryId());
            memoryDTO.setSystem(systemUsed != null);
        }
        return memoryDTOs;
    }

    @Override
    @Transactional
    public void deleteByMemoryId(String memoryId) {
        Long userId = UserContext.getUser().getUserId();
        Memory memory = lambdaQuery().eq(Memory::getMemoryId, memoryId).one();
        if (memory == null){
            throw new RuntimeException("对话记录不存在！");
        }
        if (!memory.getUserId().equals(userId)){
            throw new RuntimeException("删除错误！");
        }
        lambdaUpdate().eq(Memory::getMemoryId, memoryId).remove();
        storageTextMapper.deleteByMemoryId(memoryId);
        chatMapper.deleteByMemoryId(memoryId);
    }
}
