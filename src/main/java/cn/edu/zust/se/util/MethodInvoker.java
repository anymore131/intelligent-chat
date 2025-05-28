package cn.edu.zust.se.util;

import java.lang.reflect.Method;

public class MethodInvoker {
    public static Object invokeMethod(String className, String methodName, Object... args) {
        try {
            // 1. 加载类
            Class<?> clazz = Class.forName(className);
            // 2. 获取方法参数类型
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            // 3. 获取方法
            Method method = clazz.getMethod(methodName, paramTypes);
            // 4. 创建实例并调用方法
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
