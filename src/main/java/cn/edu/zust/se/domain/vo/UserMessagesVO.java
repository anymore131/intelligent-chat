package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMessagesVO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String content;
    private Integer contentType;
    private LocalDateTime createTime;
}
