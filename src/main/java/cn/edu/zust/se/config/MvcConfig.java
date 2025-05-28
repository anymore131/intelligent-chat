package cn.edu.zust.se.config;

import cn.edu.zust.se.interceptors.LoginInterceptor;
import cn.edu.zust.se.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties
public class MvcConfig implements WebMvcConfigurer {
    private final JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1.添加拦截器
        LoginInterceptor loginInterceptor = new LoginInterceptor(jwtUtil);
        List<String> patterns = new ArrayList<>();
        patterns.add("/user/login");
        patterns.add("/user/register");
        patterns.add("/error");
        patterns.add("/user/chat");
        registry
                .addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(patterns);
    }
}
