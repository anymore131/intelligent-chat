package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemMessageQuery extends PageQuery {
    private Long userId;
    private String name;
    private String description;
    private String accessPolicy;
    private Integer usedNumber;
    private LocalDateTime startCreateTime;
    private LocalDateTime endCreateTime;
}
