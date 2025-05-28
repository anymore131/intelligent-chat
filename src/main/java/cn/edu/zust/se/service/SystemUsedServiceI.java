package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.vo.SystemUsedVO;
import cn.edu.zust.se.domain.po.SystemUsed;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-26
 */
public interface SystemUsedServiceI extends IService<SystemUsed> {
    SystemUsedVO create(Long systemId);
    void delete(Long id);
    List<SystemUsedVO> getByUserId();
}
