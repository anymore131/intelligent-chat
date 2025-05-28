package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQuery extends PageQuery {
    private String userName;
    private String name;
    private Integer admin;
    private LocalDateTime startCreateTime;
    private LocalDateTime endCreateTime;
}
