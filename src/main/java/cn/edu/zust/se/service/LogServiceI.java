package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.Log;
import cn.edu.zust.se.domain.query.LogQuery;
import cn.edu.zust.se.domain.vo.LogVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author anymore131
 * @since 2025-05-09
 */
public interface LogServiceI extends IService<Log> {
    PageDTO<LogVO> page(LogQuery query);
}
