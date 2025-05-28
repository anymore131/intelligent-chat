package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.query.SystemMessageQuery;
import cn.edu.zust.se.domain.vo.SystemMessageVO;
import cn.edu.zust.se.domain.po.SystemMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-25
 */
public interface SystemMessageServiceI extends IService<SystemMessage> {
    SystemMessageVO createByUserId(Long userId,String description, String name, String accessPolicy);
    List<SystemMessageVO> getByUserId();
    void usedUp(Long systemId);
    void deleteById(Long id);
    void change(SystemMessage systemMessage);
    SystemMessage getByMemoryId(String memoryId);
    Long countSystem();
    PageDTO<SystemMessageVO> pageByQuery(SystemMessageQuery query);
    void create(SystemMessageVO systemMessageVO);
    void updateByVO(SystemMessageVO systemMessageVO);
}
