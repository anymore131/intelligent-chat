package cn.edu.zust.se.service;

import cn.edu.zust.se.handler.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadService {
    private Thread thread;
    private final CountServiceI countService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void startTask(Long id) {
        if (running.get()) {
            return; // 已经在运行
        }
        running.set(true);
        thread = new Thread(() -> {
            while (running.get()) {
                String json = countService.getModelTimes();
                MyWebSocketHandler.sendAdminMessage(id,json,1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("线程中断", e);
                }
            }
        });
        thread.start();
    }

    public void stopTask() {
        running.set(false);
        if (thread != null) {
            thread.interrupt();
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
