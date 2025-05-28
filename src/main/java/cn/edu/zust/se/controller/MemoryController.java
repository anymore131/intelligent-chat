package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.MemoryDTO;
import cn.edu.zust.se.service.MemoryServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memory")
@RequiredArgsConstructor
public class MemoryController {
    private final MemoryServiceI memoryService;

    // 创建对话
    @PostMapping
    public R<MemoryDTO> create(){
        MemoryDTO memoryDTO = memoryService.createByUserId();
        return R.ok(memoryDTO);
    }

    // 获取对话列表
    @GetMapping
    public R<List<MemoryDTO>> getMemoryIds(){
        List<MemoryDTO> memoryDTOList = memoryService.getMemoryIdsByUserId();
        return R.ok(memoryDTOList);
    }

    // 修改对话名称
    @PutMapping("/changeName")
    public R<String> changeName(@RequestParam(value = "memoryId") String memoryId,
                                @RequestParam(value = "name") String name){
        memoryService.changeName(memoryId,name);
        return R.ok("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam(value = "memoryId") String memoryId){
        memoryService.deleteByMemoryId(memoryId);
        return R.ok("删除成功");
    }
}
