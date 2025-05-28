package cn.edu.zust.se.task;

import cn.edu.zust.se.handler.MyWebSocketHandler;
import cn.edu.zust.se.service.*;
import cn.edu.zust.se.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class Task {
    private final CountServiceI countService;

    @Scheduled(cron = "0 * * * * ?")//每分钟执行一次
    public void processLastMessage(){
        // todo
    }

    @Scheduled(cron = "0 0 0 * * ?")//每天0点执行
    public void resetsNumber(){
        countService.updateDailyCount();
        // todo
    }
}
