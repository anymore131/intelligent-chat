package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserContactVO {
    private Long id;
    private Long userId;
    private String userName;
    private Long contactId;
    private String contactName;
    private String avatar;
    private Integer contactType;
    private Long groupId;
    private Integer status;
    private LocalDateTime lastContactTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
