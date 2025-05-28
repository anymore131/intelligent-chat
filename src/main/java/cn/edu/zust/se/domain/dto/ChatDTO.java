package cn.edu.zust.se.domain.dto;

import lombok.Data;

@Data
public class ChatDTO {
    private Long ragId = null;
    private String memoryId;
    private String message;
    private boolean deep = false;
    private boolean web = false;
}
