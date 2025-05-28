package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.vo.SystemUsedVO;
import cn.edu.zust.se.service.SystemUsedServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/system-used")
@RequiredArgsConstructor
public class SystemUsedController {
    public final SystemUsedServiceI systemUsedService;

    @PostMapping
    public R<SystemUsedVO> create(@RequestParam(value = "systemId") Long systemId) {
        SystemUsedVO systemUsedVO = systemUsedService.create(systemId);
        return R.ok(systemUsedVO);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable(value = "id") Long id) {
        systemUsedService.delete(id);
        return R.ok();
    }

    @GetMapping()
    public R<List<SystemUsedVO>> getByUserId() {
        List<SystemUsedVO> systemUsedVOs = systemUsedService.getByUserId();
        return R.ok(systemUsedVOs);
    }
}
