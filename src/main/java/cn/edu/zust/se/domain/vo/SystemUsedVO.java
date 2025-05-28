package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemUsedVO {
    private Long id;
    private Long systemId;
    private String memoryId;
    private LocalDateTime createTime;
}
