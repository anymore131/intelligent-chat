package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.PageQuery;
import cn.edu.zust.se.domain.dto.LanguageModelDTO;
import cn.edu.zust.se.domain.po.LanguageModel;
import cn.edu.zust.se.domain.po.LanguagePlatform;
import cn.edu.zust.se.domain.query.LanguageModelQuery;
import cn.edu.zust.se.domain.vo.LanguageModelVO;
import cn.edu.zust.se.domain.vo.LanguageVO;
import cn.edu.zust.se.enums.ModelType;
import cn.edu.zust.se.mapper.LanguageModelMapper;
import cn.edu.zust.se.service.LanguageModelServiceI;
import cn.edu.zust.se.service.LanguagePlatformServiceI;
import cn.edu.zust.se.service.UserServiceI;
import cn.edu.zust.se.service.aiService.Assistant;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.MethodInvoker;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import io.lettuce.core.dynamic.support.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author anymore131
 * @since 2025-04-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageModelServiceImpl extends ServiceImpl<LanguageModelMapper, LanguageModel> implements LanguageModelServiceI {
    private final LanguagePlatformServiceI languagePlatformService;
    private final LanguageModelMapper languageModelMapper;
    private final UserServiceI userService;
    private final RedisTemplate redisTemplate;

    @Override
    public List<LanguageModel> adminGetByPlatformId(Long platformId) {
        QueryWrapper<LanguageModel> wrapper = new QueryWrapper<>();
        wrapper.eq("platform_id", platformId);
        return null;
    }

    @Override
    public List<LanguageModel> getByPlatformId(Long platformId) {
        QueryWrapper<LanguageModel> wrapper = new QueryWrapper<>();
        wrapper.eq("platform_id", platformId)
                .eq("used", 1);
        List<LanguageModel> list = list(wrapper);
        if (list.isEmpty()){
            return null;
        }
        return list;
    }

    @Override
    @Transactional
    public boolean adminSaveLanguageModel(LanguageModel languageModel) {
        checkModel(languageModel);
        languageModel.setCreateTime(LocalDateTime.now());
        return save(languageModel);
    }

    @Override
    public List<LanguageModelDTO> getModelByType(String type) {
        try {
            List<LanguageModelDTO> cachedModels = (List<LanguageModelDTO>) redisTemplate.opsForList().range("model:" + type, 0, -1);
            if (cachedModels != null && !cachedModels.isEmpty()) {
                return cachedModels;
            }
            Map<String, List<LanguageModelDTO>> map = modelInitInRedis();
            return map.get(type);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public PageDTO<LanguageVO> pageByQuery(LanguageModelQuery query) {
        Page<LanguageVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LanguageVO> iPage = languageModelMapper.pageByQuery(page, query);
        if (iPage == null){
            return new PageDTO<>(0L,0L, null);
        }
        List<LanguageVO> records = iPage.getRecords();
        for (LanguageVO record : records){
            record.setUserName(userService.getUser(record.getUserId()).getUserName());
        }
        PageDTO<LanguageVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(records);
        return pageDTO;
    }

    @Override
    @Transactional
    public boolean updateLanguageModel(LanguageModel languageModel) {
        LanguageModel l = getById(languageModel.getId());
        if (l == null){
            throw new RuntimeException("模型不存在");
        }
        ReflectionUtils.doWithFields(LanguageModel.class, field -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                return;
            }
            field.setAccessible(true);
            Object value = field.get(languageModel);
            if (value != null) {
                field.set(l, value);
            }
        });
        if (l.getUsed() == 1){
            checkModel(l);
        }
        l.setUpdateTime(LocalDateTime.now());
        return updateById(l);
    }

    @Override
    @Transactional
    public void deleteLanguageModel(Long id) {
        LanguageModel l = getById(id);
        if (l == null){
            throw new RuntimeException("模型不存在");
        }
        if (l.getUsed() != 0){
            throw new RuntimeException("模型正在使用中，请先取消使用");
        }
        removeById(id);
    }

    @Override
    public List<LanguageModelDTO> getModelByUsed() {
        List<LanguageModelDTO> list = languageModelMapper.getUsedModel();
        if (list.isEmpty()){
            return List.of();
        }
        return list;
    }

    @Override
    public void initModelRedis() {
        modelInitInRedis();
    }

    private void checkModel(LanguageModel languageModel){
        LanguagePlatform languagePlatform = languagePlatformService.getById(languageModel.getPlatformId());
        if (languagePlatform == null){
            throw new RuntimeException("平台不存在");
        }
        String type = languageModel.getType();
        String methodName;
        if (type.equals("Tool")){
            methodName = languagePlatform.getPlatform() + "ChatModel";
            ChatLanguageModel m = (ChatLanguageModel) MethodInvoker.invokeMethod(
                    "cn.edu.zust.se.config.LLMConfig",
                    methodName,
                    languagePlatform.getApiKey(),
                    languageModel.getModelName());
            if (m == null){
                throw new RuntimeException("模型初始化错误！");
            }
            try {
                ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(1);
                String s = AiServices.builder(Assistant.class)
                        .chatMemory(chatMemory)
                        .chatLanguageModel(m)
                        .build().chat("你好(回复我你好)！");
            } catch (Exception e) {
                log.error("模型添加错误！", e);
                throw new RuntimeException("模型添加错误！");
            }
            return;
        }
        if (type.equals("Chat")){
            type = "";
        }
        methodName = languagePlatform.getPlatform() + type + "StreamingChatModel";
        StreamingChatLanguageModel m = (StreamingChatLanguageModel) MethodInvoker.invokeMethod(
                "cn.edu.zust.se.config.LLMConfig",
                methodName,
                languagePlatform.getApiKey(),
                languageModel.getModelName());
        if (m == null){
            throw new RuntimeException("模型初始化错误！");
        }
        try {
            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(1);
            Flux<String> stream = AiServices
                    .builder(Assistant.class)
                    .chatMemory(chatMemory)
                    .streamingChatLanguageModel(m)
                    .build().stream("你好(回复我你好)！");
            stream.subscribe(System.out::println);
        } catch (Exception e) {
            log.error("模型添加错误！", e);
            throw new RuntimeException("模型添加错误！");
        }
    }

    private Map<String, List<LanguageModelDTO>> modelInitInRedis(){
        Map<String, List<LanguageModelDTO>> map = new HashMap<>();
        for (ModelType modelType : ModelType.values()) {
            List<LanguageModelDTO> modelDTOS = languageModelMapper.getModelDTOByType(modelType.getType());
            String type = modelType.getType();
            for (LanguageModelDTO modelDTO : modelDTOS){
                if (modelType.getType().equals("Tool")){
                    modelDTO.setMethodName(modelDTO.getPlatform() + "ChatModel");
                    continue;
                }
                if (modelType.getType().equals("Chat")){
                    type = "";
                }
                modelDTO.setMethodName(modelDTO.getPlatform() + type + "StreamingChatModel");
            }
            redisTemplate.delete("model:" + modelType.getType());
            if (!modelDTOS.isEmpty()) {
                redisTemplate.opsForList().rightPushAll("model:" + modelType.getType(), modelDTOS);
            }
            map.put(modelType.getType(), modelDTOS);
        }
        return map;
    }
}
