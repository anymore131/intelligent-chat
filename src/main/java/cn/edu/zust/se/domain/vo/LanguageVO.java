package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LanguageVO {
    private Long id;
    private Long platformId;
    private String modelName;
    private String type;
    private Integer used;
    private String platformName;
    private Long userId;
    private String userName;
    private String platform;
    private String apiAddress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
