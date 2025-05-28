package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LanguageModelVO {
    private Long id;
    private Long platformId;
    private String modelName;
    private String type;
    private Integer used;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
