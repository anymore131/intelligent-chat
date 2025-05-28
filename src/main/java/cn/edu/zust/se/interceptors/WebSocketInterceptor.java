package cn.edu.zust.se.interceptors;

import cn.edu.zust.se.handler.MyWebSocketHandler;
import cn.edu.zust.se.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class WebSocketInterceptor implements HandshakeInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyWebSocketHandler.class);
    private final JwtUtil jwtUtil;

    //前置拦截一般用来注册用户信息，绑定 WebSocketSession
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        log.info("前置拦截~~");
        if (!(request instanceof ServletServerHttpRequest)) return true;
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = servletRequest.getParameter("token");
        if (token == null) {
            token = servletRequest.getHeader("Authorization");
            if (token == null){
                throw new RuntimeException("ws无权访问！");
            }
        }
        long userId = Long.parseLong(jwtUtil.parseToken(token).getSubject());
        attributes.put("id", Long.toString(userId));
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler, @NotNull Exception exception) {
        log.info("后置拦截~~");
    }
}
