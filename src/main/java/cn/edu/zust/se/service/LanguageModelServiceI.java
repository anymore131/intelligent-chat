package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.LanguageModelDTO;
import cn.edu.zust.se.domain.po.LanguageModel;
import cn.edu.zust.se.domain.query.LanguageModelQuery;
import cn.edu.zust.se.domain.vo.LanguageModelVO;
import cn.edu.zust.se.domain.vo.LanguageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-26
 */
public interface LanguageModelServiceI extends IService<LanguageModel> {
    List<LanguageModel> adminGetByPlatformId(Long platformId);
    List<LanguageModel> getByPlatformId(Long platformId);
    boolean adminSaveLanguageModel(LanguageModel languageModel);
    List<LanguageModelDTO> getModelByType(String type);
    PageDTO<LanguageVO> pageByQuery(LanguageModelQuery query);
    boolean updateLanguageModel(LanguageModel languageModel);
    void deleteLanguageModel(Long id);
    List<LanguageModelDTO> getModelByUsed();
    void initModelRedis();
}
