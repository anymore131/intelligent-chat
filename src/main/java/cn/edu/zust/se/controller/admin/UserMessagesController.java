package cn.edu.zust.se.controller.admin;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.query.TotalMessageQuery;
import cn.edu.zust.se.domain.vo.UserMessagesVO;
import cn.edu.zust.se.service.UserMessagesServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController("adminUserMessages")
@RequestMapping("/admin/user-messages")
@RequiredArgsConstructor
public class UserMessagesController {
    private final UserMessagesServiceI userMessagesService;

    @PostMapping("/page")
    public R<PageDTO<UserMessagesVO>> getMessages(@RequestBody TotalMessageQuery query){
        PageDTO<UserMessagesVO> pageDTO = userMessagesService.pageByQuery(query);
        return R.ok(pageDTO);
    }

    @PutMapping("/update")
    public R<Void> updateMessages(@RequestBody UserMessages userMessages){
        userMessagesService.updateMessages(userMessages);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    public R<Void> deleteMessages(@PathVariable("id") Long id){
        userMessagesService.deleteById(id);
        return R.ok();
    }

    @PostMapping("/create")
    public R<Void> createMessages(@RequestBody UserMessages userMessages){
        userMessagesService.createMessages(userMessages);
        return R.ok();
    }

    @PostMapping(value = "/createWithFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> createWithFileMessages(@RequestParam(value = "senderId") Long senderId,
                                          @RequestParam(value = "receiverId") Long receiverId,
                                          @RequestParam(value = "contentType") Integer contentType,
                                          @RequestParam(value = "createTime", required = false) String createTime,
                                          @RequestPart("file") MultipartFile file){
        UserMessages userMessages = new UserMessages();
        userMessages.setSenderId(senderId);
        userMessages.setReceiverId(receiverId);
        userMessages.setContentType(contentType);
        if (createTime != null){
            userMessages.setCreateTime(LocalDateTime.parse(createTime));
        }
        userMessagesService.createWithFileMessages(userMessages,file);
        return R.ok();
    }
}
