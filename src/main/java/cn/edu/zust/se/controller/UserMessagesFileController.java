package cn.edu.zust.se.controller;


import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.UserMessagesFileDTO;
import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.service.UserMessagesFileServiceI;
import cn.edu.zust.se.service.UserMessagesServiceI;
import cn.edu.zust.se.service.impl.UserMessagesFileServiceImpl;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author anymore131
 * @since 2025-05-13
 */
@RestController
@RequestMapping("/user-messages-file")
@RequiredArgsConstructor
public class UserMessagesFileController {
    private final UserMessagesFileServiceI userMessagesFileService;
    private final UserMessagesServiceI userMessagesServiceI;

    @PostMapping(value = "/setFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> setFile(@RequestParam("receiverId") Long receiverId,
                     @RequestParam("contentType") Integer contentType,
                     @RequestPart("file") MultipartFile file){
        Long senderId = UserContext.getUser().getUserId();
        UserMessages userMessages = new UserMessages();
        userMessages.setSenderId(senderId);
        userMessages.setReceiverId(receiverId);
        userMessages.setContentType(contentType);
        userMessages.setCreateTime(LocalDateTime.now());
        UserMessagesFileDTO userMessagesFile = userMessagesFileService.setFile(file);
        userMessages.setContent(userMessagesFile.toJson());
        userMessagesServiceI.sendFileMessage(userMessages,file.getOriginalFilename());
        return R.ok(userMessagesFile.getFilePath());
    }
}
