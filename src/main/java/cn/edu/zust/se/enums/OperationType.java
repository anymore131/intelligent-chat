package cn.edu.zust.se.enums;

import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public enum OperationType {
    UNKNOWN("其他", "unknown"),
    QUERY("查询", "get,query,select,list,find"),
    INSERT("新增", "add,create,insert,save"),
    UPDATE("修改", "update,modify,edit"),
    DELETE("删除", "delete,remove,del"),
    IMPORT("导入", "import"),
    EXPORT("导出", "export"),
    LOGIN("登录", "login,signin"),
    LOGOUT("登出", "logout,signout"),
    WEBSOCKET("连接", "websocket"),
    UPLOAD("上传", "upload"),
    DOWNLOAD("下载", "download");

    private final String name;
    private final String[] methodPrefixes;

    OperationType(String name, String prefixes) {
        this.name = name;
        this.methodPrefixes = prefixes.split(",");
    }

    /**
     * 根据方法名解析操作类型
     */
    public static OperationType resolveByMethodName(String methodName) {
        if (methodName == null) return UNKNOWN;

        String lowerMethodName = methodName.toLowerCase();
        for (OperationType type : values()) {
            for (String prefix : type.getMethodPrefixes()) {
                if (lowerMethodName.startsWith(prefix)) {
                    return type;
                }
            }
        }
        return UNKNOWN;
    }

    public static OperationType resolveOperationType(Method method) {
        String methodName = method.getName().toLowerCase();

        if (methodName.startsWith("get") || methodName.startsWith("page") || methodName.startsWith("select") || methodName.startsWith("list")) {
            return QUERY;
        } else if (methodName.startsWith("add") || methodName.startsWith("create") || methodName.startsWith("insert")) {
            return INSERT;
        } else if (methodName.startsWith("update") || methodName.startsWith("modify")) {
            return UPDATE;
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return DELETE;
        } else if (methodName.startsWith("import")) {
            return IMPORT;
        } else if (methodName.startsWith("export")) {
            return EXPORT;
        } else {
            return UNKNOWN;
        }
    }
}
