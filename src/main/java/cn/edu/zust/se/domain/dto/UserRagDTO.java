package cn.edu.zust.se.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRagDTO {
    private Long id;
    private String name;
    private LocalDateTime createTime;
}
