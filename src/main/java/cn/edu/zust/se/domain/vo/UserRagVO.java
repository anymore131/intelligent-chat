package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRagVO {
    private Long id;
    private Long userId;
    private String userName;
    private String name;
    private LocalDateTime createTime;
}
