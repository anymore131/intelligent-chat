package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageFileVO {
    private Long id;
    private Long userId;
    private String userName;
    private String fileName;
    private String filePath;
    private LocalDateTime createTime;
}
