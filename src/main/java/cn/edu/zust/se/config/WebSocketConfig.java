package cn.edu.zust.se.config;

import cn.edu.zust.se.handler.MyWebSocketHandler;
import cn.edu.zust.se.interceptors.WebSocketInterceptor;
import cn.edu.zust.se.service.UserContactServiceI;
import cn.edu.zust.se.service.UserMessagesServiceI;
import cn.edu.zust.se.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final UserMessagesServiceI userMessagesServiceI;
    private final UserContactServiceI userContactService;
    private final JwtUtil jwtUtil;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(userMessagesServiceI, userContactService)
                        , "/ws/chat")//设置连接路径和处理
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketInterceptor(jwtUtil));//设置拦截器
    }
}
