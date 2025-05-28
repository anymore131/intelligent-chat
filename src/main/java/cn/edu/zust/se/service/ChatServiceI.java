package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.po.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatServiceI extends IService<Chat> {
    boolean streamCheck(String memoryId);
}
