package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.UserRagDTO;
import cn.edu.zust.se.domain.po.UserRag;
import cn.edu.zust.se.domain.query.UserRagQuery;
import cn.edu.zust.se.domain.vo.UserRagVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-23
 */
public interface UserRagServiceI extends IService<UserRag> {
    boolean checkRag(Long ragId);
    UserRagDTO createRag(String name);
    void createRag(UserRag userRag);
    List<UserRagDTO> getUserRagList();
    void deleteById(Long id);
    void updateRagName(Long id,String name);
    void updateRag(UserRag userRag);
    Long countUserRag();
    PageDTO<UserRagVO> pageByQuery(UserRagQuery query);
}
