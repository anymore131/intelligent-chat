package cn.edu.zust.se.domain.webSocket;

import lombok.Data;

@Data
public class AdminMessageData {
    private Long toId;
    private String data;
    // 1: Model实时消息
    private Integer contentType;

    public AdminMessageData(Long toId, String data, Integer contentType) {
        this.toId = toId;
        this.data = data;
        this.contentType = contentType;
    }
}
