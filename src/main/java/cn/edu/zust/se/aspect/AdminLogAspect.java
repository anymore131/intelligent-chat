package cn.edu.zust.se.aspect;

import cn.edu.zust.se.domain.po.Log;
import cn.edu.zust.se.enums.OperationType;
import cn.edu.zust.se.service.LogServiceI;
import cn.edu.zust.se.util.IpUtils;
import cn.edu.zust.se.util.UserContext;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminLogAspect {
    private final LogServiceI logService;
    private final HttpServletRequest request;

    // 定义切点：拦截 admin 包下的所有方法
    @Pointcut("execution(* cn.edu.zust.se.controller.admin..*.*(..)) && " +
            "!within(cn.edu.zust.se.controller.admin.LogController) && " +
            "!within(cn.edu.zust.se.controller.admin.DailyCountController)")
    public void adminPackage() {}

    @Before("adminPackage()")
    public void beforeMethod(JoinPoint joinPoint) {
        Integer admin = UserContext.getUser().getAdmin();
        if (admin != 1 && admin != 2) {
//            log.info("[Admin操作日志] 用户{}无权限访问",  UserContext.getUser().getUserId());
            throw new RuntimeException("无权限访问！");
        }
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
//        log.info("[Admin操作日志] 开始执行: {}.{}, 参数: {}", className, methodName, args);
    }

    @AfterReturning(pointcut = "adminPackage()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
//        log.info("[Admin操作日志] 方法返回: {}, 结果: {}", methodName, result);
    }

    @Around("adminPackage()")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Long userId = UserContext.getUser().getUserId();
        Log log = new Log();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            log.setClassName(joinPoint.getTarget().getClass().getName());
            log.setMethodName(signature.getName());
            log.setModule("admin");
            log.setOperationType(OperationType.resolveOperationType(signature.getMethod()).getName());
            log.setUserId(userId);
            log.setOperationIp(IpUtils.getIpAddr(request));
            Object[] args = joinPoint.getArgs();
            Map<String, Object> paramMap = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) arg;
                    paramMap.put("file_" + i, file.getOriginalFilename()); // 只记录文件名
                } else {
                    paramMap.put("arg_" + i, arg);
                }
            }
            log.setMethodParams(JSON.toJSONString(paramMap));
            Object result = joinPoint.proceed();
            log.setReturnValue(JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            log.setCreateTime(LocalDateTime.now());
            log.setExecutionTime(System.currentTimeMillis() - startTime);
            logService.save(log);
        }
    }

    @AfterThrowing(pointcut = "adminPackage()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
//        log.error("[Admin操作日志] 方法异常: {}, 异常信息: {}", methodName, ex.getMessage(), ex);
    }
}
