package cn.edu.zust.se.domain.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private long id;
    private String token;
    private String userName;
    private String name;
    private boolean admin;
}
