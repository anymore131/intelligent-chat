package cn.edu.zust.se.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginFormDTO {
    @NotNull(message = "用户名不能为空")
    private String userName;
    @NotNull(message = "密码不能为空")
    private String password;

    private boolean admin;
}
