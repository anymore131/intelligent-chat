package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.RagFile;
import cn.edu.zust.se.domain.query.RagFileQuery;
import cn.edu.zust.se.domain.vo.RagFileVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-23
 */
public interface RagFileServiceI extends IService<RagFile> {
    Integer load(Long ragId, MultipartFile... files);
    List<RagFileVO> getFiles(Long ragId);

    PageDTO<RagFileVO> adminPageByQuery(RagFileQuery query);
}
