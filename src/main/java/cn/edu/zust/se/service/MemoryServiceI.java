package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.dto.MemoryDTO;
import cn.edu.zust.se.domain.po.Memory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MemoryServiceI extends IService<Memory> {
    void input(String memoryId,Long userId,String name);
    MemoryDTO createByUserId();
    void initMemory(String memoryId,String message);
    void changeName(String memoryId,String name);
    MemoryDTO getMemoryById(Long id);
    Memory getMemoryByMemoryId(String memoryId);
    List<MemoryDTO> getMemoryIdsByUserId();
    void deleteByMemoryId(String memoryId);
}
