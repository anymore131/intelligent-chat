package cn.edu.zust.se.controller.admin;


import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.po.LanguagePlatform;
import cn.edu.zust.se.domain.vo.LanguagePlatformVO;
import cn.edu.zust.se.service.LanguagePlatformServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-26
 */
@RestController("adminLanguagePlatform")
@RequestMapping("/admin/language-platform")
@RequiredArgsConstructor
public class LanguagePlatformController {
    private final LanguagePlatformServiceI languagePlatformService;

    @GetMapping("/getAll")
    public R<List<LanguagePlatformVO>> getLanguagePlatforms() {
        List<LanguagePlatformVO> languagePlatforms = languagePlatformService.getAllLanguagePlatform();
        return R.ok(languagePlatforms);
    }

    @PostMapping("/add")
    public R<LanguagePlatformVO> addLanguagePlatform(@RequestBody LanguagePlatform languagePlatform) {
        LanguagePlatformVO languagePlatformVO = languagePlatformService.createPlatform(languagePlatform);
        return R.ok(languagePlatformVO);
    }

    @PutMapping("/update")
    public R<LanguagePlatformVO> updateLanguagePlatform(@RequestBody LanguagePlatform languagePlatform) {
        LanguagePlatformVO languagePlatformVO = languagePlatformService.updatePlatform(languagePlatform);
        return R.ok(languagePlatformVO);
    }

    @DeleteMapping("/delete/{id}")
    public R<String> deleteLanguagePlatform(@PathVariable Long id) {
        languagePlatformService.deleteById(id);
        return R.ok("删除成功");
    }

    @GetMapping("/get/{userId}")
    public R<List<LanguagePlatformVO>> getLanguagePlatformsByUserId(@PathVariable Long userId) {
        List<LanguagePlatformVO> languagePlatforms = languagePlatformService.getAllLanguagePlatform();
        return R.ok(languagePlatforms);
    }
}
