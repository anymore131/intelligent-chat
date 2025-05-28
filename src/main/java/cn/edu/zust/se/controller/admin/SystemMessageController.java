package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.query.SystemMessageQuery;
import cn.edu.zust.se.domain.vo.SystemMessageVO;
import cn.edu.zust.se.service.SystemMessageServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("adminSystemMessage")
@RequestMapping("/admin/system-message")
@RequiredArgsConstructor
public class SystemMessageController {
    private final SystemMessageServiceI systemMessageService;

    @PostMapping("/page")
    public R<PageDTO<SystemMessageVO>> pageByPage(@RequestBody SystemMessageQuery query){
        PageDTO<SystemMessageVO> page = systemMessageService.pageByQuery(query);
        return R.ok(page);
    }

    @PostMapping("/create")
    public R<String> create(@RequestBody SystemMessageVO systemMessageVO){
        systemMessageService.create(systemMessageVO);
        return R.ok("创建成功");
    }

    @PutMapping("/update")
    public R<String> update(@RequestBody SystemMessageVO systemMessageVO){
        systemMessageService.updateByVO(systemMessageVO);
        return R.ok("修改成功");
    }
}
