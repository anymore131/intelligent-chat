package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.dto.LanguageModelDTO;
import cn.edu.zust.se.service.*;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CountServiceImpl implements CountServiceI {
    private final SystemMessageServiceI systemMessageService;
    private final UserRagServiceI userRagService;
    private final UserServiceI userService;
    private final LanguageModelServiceI languageModelService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> init(String type) {
        Map<String, Object> map = new HashMap<>();
        String prefix = "daily_count:" + type + ":";
        Set<String> keys = redisTemplate.keys(prefix + "*");
        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            map.put(key.substring(prefix.length()), value);
        }
        return map;
    }

    @Override
    public void updateDailyCount() {
        redisTemplate.opsForValue().set("daily_count:chat:chat",0L);
        redisTemplate.opsForValue().set("daily_count:chat:deep",0L);
        redisTemplate.opsForValue().set("daily_count:chat:web",0L);
        redisTemplate.opsForValue().set("daily_count:chat:rag",0L);
        redisTemplate.opsForValue().set("daily_count:chat:multi",0L);
        redisTemplate.opsForValue().set("daily_count:normal:admin",userService.countAdmin());
        redisTemplate.opsForValue().set("daily_count:normal:user",userService.countUser());
        redisTemplate.opsForValue().set("daily_count:normal:rag",userRagService.countUserRag());
        redisTemplate.opsForValue().set("daily_count:normal:system",systemMessageService.countSystem());
    }

    @Override
    public String getModelTimes() {
        JSONObject json = new JSONObject();
        List<LanguageModelDTO> list = languageModelService.getModelByUsed();
        for (LanguageModelDTO languageModelDTO : list) {
            String key = "daily_count:model:" + languageModelDTO.getPlatformName() + "-" + languageModelDTO.getPlatform() + "-" + languageModelDTO.getModelName();
            if (redisTemplate.hasKey(key)){
                json.put(languageModelDTO.getPlatformName() + ": " + languageModelDTO.getModelName(),redisTemplate.opsForValue().get(key));
            }else{
                json.put(languageModelDTO.getPlatformName() + ": " + languageModelDTO.getModelName(),0);
            }
        }
        return json.toJSONString();
    }
}
