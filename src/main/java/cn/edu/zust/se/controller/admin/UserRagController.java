package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.UserRagDTO;
import cn.edu.zust.se.domain.po.UserRag;
import cn.edu.zust.se.domain.query.UserRagQuery;
import cn.edu.zust.se.domain.vo.UserRagVO;
import cn.edu.zust.se.service.UserRagServiceI;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("adminUserRag")
@RequestMapping("/admin/user-rag")
@RequiredArgsConstructor
public class UserRagController {
    private final UserRagServiceI userRagService;

    @PostMapping("/page")
    public R<PageDTO<UserRagVO>> pageByQuery(@RequestBody UserRagQuery query){
        PageDTO<UserRagVO> page = userRagService.pageByQuery(query);
        return R.ok(page);
    }

    @PostMapping("/create")
    public R<Void> createRag(@RequestBody UserRag userRag){
        userRagService.createRag(userRag);
        return R.ok();
    }

    @PostMapping("/delete/{id}")
    public R<Void> deleteById(@PathVariable("id") Long id){
        userRagService.deleteById(id);
        return R.ok();
    }

    @PutMapping("/update")
    public R<Void> updateRag(@RequestBody UserRag userRag){
        userRagService.updateRag(userRag);
        return R.ok();
    }
}
