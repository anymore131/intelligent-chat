package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String userName;
    private String name;
    private Integer admin;
    private LocalDateTime createTime;
}
