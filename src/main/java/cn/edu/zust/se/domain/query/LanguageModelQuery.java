package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LanguageModelQuery extends PageQuery {
    private Long platformId = null;
    private Long userId = null;
    private String type = null;
    private String modelName = null;
    private String platformName = null;
    private Integer used = null;
}
