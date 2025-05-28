package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserChatListVO {
    private Long id;
    private Long contactId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private Integer isPinned;
    private String name;
    private Integer contactType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
