package cn.edu.zust.se.domain.dto;

import lombok.Data;

@Data
public class UserTokenDTO {
    private Long userId;
    private String userName;
    private Integer admin;
}
