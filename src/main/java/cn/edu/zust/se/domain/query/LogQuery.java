package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class LogQuery extends PageQuery {
    private String module;
    private Long userId;
    private String operationType;
    private String className;
    private LocalDateTime startCreateTime;
    private LocalDateTime endCreateTime;
    private Long minExecutionTime;
    private Integer error;
}
