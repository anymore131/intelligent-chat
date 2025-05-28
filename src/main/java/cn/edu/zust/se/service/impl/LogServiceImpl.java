package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.Log;
import cn.edu.zust.se.domain.query.LogQuery;
import cn.edu.zust.se.domain.vo.LogVO;
import cn.edu.zust.se.mapper.LogMapper;
import cn.edu.zust.se.service.LogServiceI;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author anymore131
 * @since 2025-05-09
 */
@Service
@RequiredArgsConstructor
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogServiceI {
    private final LogMapper logMapper;

    @Override
    public PageDTO<LogVO> page(LogQuery query) {
        Page<LogVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogVO> iPage = logMapper.pageByQuery(page, query);
        if (iPage == null){
            return new PageDTO<>(0L,0L, null);
        }
        PageDTO<LogVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }
}
