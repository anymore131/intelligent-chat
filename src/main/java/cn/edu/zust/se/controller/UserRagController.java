package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.UserRagDTO;
import cn.edu.zust.se.domain.po.UserRag;
import cn.edu.zust.se.service.UserRagServiceI;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-23
 */
@RestController("userUserRag")
@RequestMapping("/user-rag")
@RequiredArgsConstructor
public class UserRagController {
    private final UserRagServiceI userRagService;

    @PostMapping
    public R<UserRagDTO> add(@RequestParam("name")String name){
        UserRagDTO userRag = userRagService.createRag(name);
        return R.ok(userRag);
    }

    @DeleteMapping
    public R delete(@RequestParam("id")Long id){
        userRagService.deleteById(id);
        return R.ok();
    }

    @PutMapping
    public R update(@RequestParam("id")Long id,@RequestParam("name")String name){
        userRagService.updateRagName(id,name);
        return R.ok();
    }

    @GetMapping
    public R<List<UserRagDTO>> get(){
        List<UserRagDTO> list = userRagService.getUserRagList();
        return R.ok(list);
    }
}
