package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.query.LogQuery;
import cn.edu.zust.se.domain.vo.LogVO;
import cn.edu.zust.se.service.LogServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author anymore131
 * @since 2025-05-09
 */
@RestController("adminLog")
@RequestMapping("/admin/log")
@RequiredArgsConstructor
public class LogController {
    private final LogServiceI logService;

    @RequestMapping("/page")
    public R<PageDTO<LogVO>> page(@RequestBody LogQuery query) {
        PageDTO<LogVO> page = logService.page(query);
        return R.ok(page);
    }
}
