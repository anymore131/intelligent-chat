package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StorageTextVO {
    private Long id;
    private String text;
    private String type;
    private String contentsType;
    private LocalDateTime createTime;
}
