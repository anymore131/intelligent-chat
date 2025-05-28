package cn.edu.zust.se.util;

import cn.edu.zust.se.domain.dto.UserTokenDTO;

public class UserContext {
    private static final ThreadLocal<UserTokenDTO> threadLocal = new ThreadLocal<>();

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userToken 用户token校验
     */
    public static void setUser(UserTokenDTO userToken) {
        threadLocal.set(userToken);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static UserTokenDTO getUser() {
        return threadLocal.get();
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        threadLocal.remove();
    }
}
