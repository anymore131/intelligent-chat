package cn.edu.zust.se.domain.webSocket;

import lombok.Data;

@Data
public class WebSocketRequest {
    private Long toId;
    private String content;
    private Integer contentType;
}
