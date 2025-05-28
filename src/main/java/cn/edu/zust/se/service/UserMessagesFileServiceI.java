package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.UserMessagesFileDTO;
import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.po.UserMessagesFile;
import cn.edu.zust.se.domain.query.MessageFileQuery;
import cn.edu.zust.se.domain.vo.MessageFileVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author anymore131
 * @since 2025-05-13
 */
public interface UserMessagesFileServiceI extends IService<UserMessagesFile> {
    UserMessagesFileDTO getFile(String fileUUID);
    UserMessagesFileDTO setFile(MultipartFile file);

    PageDTO<MessageFileVO> adminPageByQuery(MessageFileQuery query);
    UserMessagesFileDTO setFile(MultipartFile file, Long userId);
}
