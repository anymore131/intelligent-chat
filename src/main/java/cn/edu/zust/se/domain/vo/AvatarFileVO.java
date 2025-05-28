package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvatarFileVO {
    private Long id;
    private Long userId;
    private String userName;
    private String path;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
