package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAvatarVO {
    private Long id;
    private Long userId;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
