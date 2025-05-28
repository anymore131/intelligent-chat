package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.po.UserRag;
import cn.edu.zust.se.domain.vo.RagFileVO;
import cn.edu.zust.se.service.RagFileServiceI;
import cn.edu.zust.se.service.UserRagServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-23
 */
@RestController
@RequestMapping("/rag-file")
@RequiredArgsConstructor
public class RagFileController {
    private final RagFileServiceI ragFileService;
    private final UserRagServiceI userRagService;

    @PostMapping(value = "/load", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> load(@RequestParam (value = "ragId") Long ragId,
                          @RequestPart("file") MultipartFile... files) {
        if (!userRagService.checkRag(ragId)){
            throw new RuntimeException("知识库获取失败！");
        }
        Integer sum = ragFileService.load(ragId,files);
        return R.ok( "一共上传文件" + files.length + "个,成功上传文件" + (files.length - sum) + "个");
    }

    @GetMapping("/list")
    public R<List<RagFileVO>> list(@RequestParam(value = "ragId") Long ragId){
        if (!userRagService.checkRag(ragId)){
            throw new RuntimeException("知识库获取失败！");
        }
        if (userRagService.lambdaQuery().eq(UserRag::getId, ragId).one() == null){
            throw new RuntimeException("向量库不存在！");
        }
        List<RagFileVO> ragFileVOS = ragFileService.getFiles(ragId);
        return R.ok(ragFileVOS);
    }
}
