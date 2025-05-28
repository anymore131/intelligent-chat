package cn.edu.zust.se.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogVO {
    private Long id;
    private String module;
    private Long userId;
    private String userName;
    private String operationType;
    private String className;
    private String methodName;
    private String methodParams;
    private String returnValue;
    private String errorMessage;
    private String operationIp;
    private LocalDateTime createTime;
    private Long executionTime;
}
