package cn.edu.zust.se.interceptors;

import cn.edu.zust.se.domain.dto.UserTokenDTO;
import cn.edu.zust.se.util.JwtUtil;
import cn.edu.zust.se.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的 token
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("无权访问！");
        }
        // 2.校验token
        Long userId = Long.valueOf(jwtUtil.parseToken(token).getSubject());
        Integer admin = jwtUtil.parseToken(token).get("admin", Integer.class);
        String userName = jwtUtil.parseToken(token).get("userName", String.class);
        UserTokenDTO userToken = new UserTokenDTO();
        userToken.setUserId(userId);
        userToken.setAdmin(admin);
        userToken.setUserName(userName);
        // 3.存入上下文
        UserContext.setUser(userToken);
        // 4.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户
        UserContext.removeUser();
    }
}
