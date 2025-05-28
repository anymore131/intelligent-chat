package cn.edu.zust.se.domain.webSocket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageData {
    private String message;
    @NotNull
    private Long toId;
    private boolean error = false;
    // 1: 文本，2: 图片，3: 文件
    // 99: 账号在其他地方登录
    private Integer contentType = 1;
    private Long id;

    public MessageData(Long id, Long toId, String message, boolean error, Integer contentType) {
        this.id = id;
        this.toId = toId;
        this.error = error;
        this.message = message;
        if (contentType != null){
            this.contentType = contentType;
        }
    }
}
