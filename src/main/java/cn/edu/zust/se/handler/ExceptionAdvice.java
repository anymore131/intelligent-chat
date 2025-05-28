package cn.edu.zust.se.handler;

import cn.edu.zust.se.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<String> handleException(Exception e){
        return R.error(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R<String> handleRuntimeException(RuntimeException e){
        return R.error(e.getMessage());
    }
}