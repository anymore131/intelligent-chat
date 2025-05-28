package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.query.MessageFileQuery;
import cn.edu.zust.se.domain.query.MessageQuery;
import cn.edu.zust.se.domain.query.TotalMessageQuery;
import cn.edu.zust.se.domain.vo.MessageFileVO;
import cn.edu.zust.se.domain.vo.UserMessagesVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserMessagesServiceI extends IService<UserMessages> {
    void sendMessage(Long senderId,Long receiverId, String content,Integer contentType);
    List<UserMessagesVO> getHistoryMessages(MessageQuery query);
    String getHistoryString(Integer number, Long receiverId);
    PageDTO<UserMessagesVO> pageByQuery(TotalMessageQuery query);
    void sendFileMessage(UserMessages userMessages,String fileName);
    void updateMessages(UserMessages userMessages);
    void deleteById(Long id);
    void createMessages(UserMessages userMessages);
    void createWithFileMessages(UserMessages userMessages, MultipartFile file);
}