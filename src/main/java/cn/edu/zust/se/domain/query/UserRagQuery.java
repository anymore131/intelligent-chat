package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRagQuery extends PageQuery {
    private Long userId;
    private String name;
    private String startCreateTime;
    private String endCreateTime;
}
