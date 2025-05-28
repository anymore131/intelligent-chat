package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemMessageVO {
    private Long id;
    private Long userId;
    private String userName;
    private String name;
    private String description;
    private String accessPolicy;
    private Long usedNumber;
    private LocalDateTime createTime;
}
