package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.vo.SystemMessageVO;
import cn.edu.zust.se.domain.po.SystemMessage;
import cn.edu.zust.se.service.SystemMessageServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-25
 */
@RestController("userSystemMessage")
@RequestMapping("/system-message")
@RequiredArgsConstructor
public class SystemMessageController {
    private final SystemMessageServiceI systemMessageService;

    @PostMapping
    public R<SystemMessageVO> create(@RequestParam(value = "name")String name,
                                     @RequestParam(value = "description")String description,
                                     @RequestParam(value = "accessPolicy")String accessPolicy){
        Long userId = UserContext.getUser().getUserId();
        SystemMessageVO systemMessageVO = systemMessageService.createByUserId(userId,description, name ,accessPolicy);
        return R.ok(systemMessageVO);
    }

    @GetMapping
    public R<List<SystemMessageVO>> getByUserId(){
        List<SystemMessageVO> systemMessageVO = systemMessageService.getByUserId();
        return R.ok(systemMessageVO);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id") Long id){
        systemMessageService.deleteById(id);
        return R.ok();
    }

    @PutMapping
    public R update(@RequestBody SystemMessage systemMessage){
        systemMessageService.change(systemMessage);
        return R.ok();
    }

    @GetMapping("/detail")
    public R<SystemMessageVO> getByMemoryId(@RequestParam(value = "memoryId") String memoryId){
        SystemMessage systemMessage = systemMessageService.getByMemoryId(memoryId);
        return R.ok(BeanUtils.copyProperties(systemMessage, SystemMessageVO.class));
    }
}
