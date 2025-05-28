package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LanguagePlatformVO {
    private Long id;
    private Long userId;
    private String userName;
    private String platform;
    private String platformName;
    private String apiKey;
    private String apiAddress;
    private LocalDateTime createTime;
}
