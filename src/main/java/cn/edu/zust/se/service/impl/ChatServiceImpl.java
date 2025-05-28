package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.po.Chat;
import cn.edu.zust.se.domain.po.Memory;
import cn.edu.zust.se.domain.po.StorageText;
import cn.edu.zust.se.mapper.ChatMapper;
import cn.edu.zust.se.service.ChatServiceI;
import cn.edu.zust.se.service.StorageTextServiceI;
import cn.edu.zust.se.service.MemoryServiceI;
import cn.edu.zust.se.util.UserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatServiceI {
    private final MemoryServiceI memoryService;

    @Override
    public boolean streamCheck(String memoryId) {
        Long userId = UserContext.getUser().getUserId();
        boolean isTrue = false;
        List<Memory> memories = memoryService.lambdaQuery().eq(Memory::getUserId, userId).list();
        for (Memory memory : memories){
            if (memory.getMemoryId().equals(memoryId)) {
                isTrue = true;
                break;
            }
        }
        return isTrue;
    }
}
