package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.UserMessagesFileDTO;
import cn.edu.zust.se.domain.query.AvatarFileQuery;
import cn.edu.zust.se.domain.query.MessageFileQuery;
import cn.edu.zust.se.domain.query.RagFileQuery;
import cn.edu.zust.se.domain.vo.AvatarFileVO;
import cn.edu.zust.se.domain.vo.MessageFileVO;
import cn.edu.zust.se.domain.vo.RagFileVO;
import cn.edu.zust.se.service.RagFileServiceI;
import cn.edu.zust.se.service.UserAvatarServiceI;
import cn.edu.zust.se.service.UserMessagesFileServiceI;
import cn.edu.zust.se.service.UserMessagesServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("adminFile")
@RequestMapping("/admin/file")
@RequiredArgsConstructor
public class FileController {
    private final RagFileServiceI  ragFileService;
    private final UserMessagesFileServiceI UserMessagesFileService;
    private final UserAvatarServiceI userAvatarService;

    @PostMapping("/messages/page")
    public R<PageDTO<MessageFileVO>> pageMessagesFile(@RequestBody MessageFileQuery query){
        PageDTO<MessageFileVO> page = UserMessagesFileService.adminPageByQuery(query);
        return R.ok(page);
    }

    @PostMapping("/rag/page")
    public R<PageDTO<RagFileVO>> pageRagFile(@RequestBody RagFileQuery query){
        PageDTO<RagFileVO> page = ragFileService.adminPageByQuery(query);
        return R.ok(page);
    }

    @PostMapping("/avatar/page")
    public R<PageDTO<AvatarFileVO>> pageAvatarFile(@RequestBody AvatarFileQuery query){
        PageDTO<AvatarFileVO> page = userAvatarService.adminPageByQuery(query);
        return R.ok(page);
    }
}
