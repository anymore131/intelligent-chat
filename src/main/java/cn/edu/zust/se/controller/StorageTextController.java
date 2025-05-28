package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.query.StorageQuery;
import cn.edu.zust.se.domain.vo.StorageTextVO;
import cn.edu.zust.se.service.StorageTextServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageTextController {
    private final StorageTextServiceI storageTextService;

    // 获取对话内容
    @PostMapping("/getTexts")
    public R<List<StorageTextVO>> getTexts(@RequestBody StorageQuery query){
        List<StorageTextVO> aiTextDTOList = storageTextService.getHistoryStorage(query);
        return R.ok(aiTextDTOList);
    }
}
