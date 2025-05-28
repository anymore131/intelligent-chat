package cn.edu.zust.se.domain.webSocket;

import lombok.Data;

@Data
public class WebSocketResponse<T> {
    private String type;
    private T data;
}
