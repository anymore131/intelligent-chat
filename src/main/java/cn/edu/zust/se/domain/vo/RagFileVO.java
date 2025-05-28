package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RagFileVO{
    private Long id;
    private Long ragId;
    private Long userId;
    private String ragName;
    private String userName;
    private String fileName;
    private String fileContent;
    private LocalDateTime createTime;
}
