package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TotalMessageQuery extends PageQuery {
    private Long[] senderId;
    private Long[] receiverId;
    private String content;
    private Integer[] contentType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
