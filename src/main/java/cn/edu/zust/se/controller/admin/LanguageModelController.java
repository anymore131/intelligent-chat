package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.po.LanguageModel;
import cn.edu.zust.se.domain.query.LanguageModelQuery;
import cn.edu.zust.se.domain.vo.LanguageModelVO;
import cn.edu.zust.se.domain.vo.LanguageVO;
import cn.edu.zust.se.service.LanguageModelServiceI;
import cn.edu.zust.se.service.ThreadService;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-26
 */
@RestController("adminLanguageModel")
@RequestMapping("/admin/language-model")
@RequiredArgsConstructor
public class LanguageModelController {
    private final LanguageModelServiceI languageModelServiceI;
    private final ThreadService threadService;

    @PostMapping("/create")
    public R<String> createLanguageModel(@RequestBody LanguageModel languageModel) {
        if (languageModelServiceI.adminSaveLanguageModel(languageModel)) {
            languageModelServiceI.initModelRedis();
            return R.ok("保存成功");
        }
        return R.error("保存失败");
    }

    @GetMapping("/{platformId}")
    public R<List<LanguageModelVO>> getLanguageModelByPlatformId(@PathVariable("platformId") Long platformId) {
        List<LanguageModel> list = languageModelServiceI.getByPlatformId(platformId);
        if (list != null) {
            return R.ok(BeanUtils.copyList(list, LanguageModelVO.class));
        }
        return R.error("获取失败");
    }

    @PostMapping("/page")
    public R<PageDTO<LanguageVO>> page(@RequestBody LanguageModelQuery query){
        PageDTO<LanguageVO> page = languageModelServiceI.pageByQuery(query);
        if (page == null) {
            return new R<>(200, "未有数据", null);
        }
        return R.ok(page);
    }

    @DeleteMapping("/delete/{id}")
    public R<String> deleteLanguageModel(@PathVariable("id") Long id) {
        languageModelServiceI.deleteLanguageModel(id);
        languageModelServiceI.initModelRedis();
        return R.error("删除成功");
    }

    @PutMapping("/update")
    public R<String> updateLanguageModel(@RequestBody LanguageModel languageModel) {
        if (languageModelServiceI.updateLanguageModel(languageModel)) {
            languageModelServiceI.initModelRedis();
            return R.ok("更新成功");
        }
        return R.error("更新失败");
    }

    @PostMapping("/times/start")
    public R<String> startTask() {
        Long id = UserContext.getUser().getUserId();
        threadService.startTask(id);
        return R.ok("Task started");
    }

    @PostMapping("/times/stop")
    public R<String> stopTask() {
        threadService.stopTask();
        return R.ok("Task stopped");
    }

    @GetMapping("/times/status")
    public R<Boolean> getTaskStatus() {
        return R.ok(threadService.isRunning());
    }
}
