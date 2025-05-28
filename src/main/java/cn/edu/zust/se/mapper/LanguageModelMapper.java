package cn.edu.zust.se.mapper;

import cn.edu.zust.se.domain.dto.LanguageModelDTO;
import cn.edu.zust.se.domain.po.LanguageModel;
import cn.edu.zust.se.domain.query.LanguageModelQuery;
import cn.edu.zust.se.domain.vo.LanguageModelVO;
import cn.edu.zust.se.domain.vo.LanguageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-26
 */
public interface LanguageModelMapper extends BaseMapper<LanguageModel> {
    @Select("select m.id, p.platform, p.api_key, m.model_name, p.platform_name " +
            "from language_model m " +
            "INNER JOIN language_platform p ON m.platform_id = p.id " +
            "where m.type = #{type} and m.used = 1")
    List<LanguageModelDTO> getModelDTOByType(String type);

    @Delete("delete from language_model where platform_id = #{platformId}")
    void deleteByPlatformId(Long platformId);

    @Select("select count(id) from language_model where platform_id = #{platformId}")
    Integer countByPlatformId(Long platformId);

    IPage<LanguageVO> pageByQuery(@Param("page") IPage<LanguageVO> page,
                                  @Param("query") LanguageModelQuery query);

    List<LanguageModelDTO> getUsedModel();
}
