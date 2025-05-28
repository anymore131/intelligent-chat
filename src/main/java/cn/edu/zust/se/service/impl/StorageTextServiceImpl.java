package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.query.StorageQuery;
import cn.edu.zust.se.domain.vo.StorageTextVO;
import cn.edu.zust.se.domain.po.Memory;
import cn.edu.zust.se.domain.po.StorageText;
import cn.edu.zust.se.domain.vo.UserMessagesVO;
import cn.edu.zust.se.mapper.StorageTextMapper;
import cn.edu.zust.se.service.MemoryServiceI;
import cn.edu.zust.se.service.StorageTextServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StorageTextServiceImpl extends ServiceImpl<StorageTextMapper, StorageText> implements StorageTextServiceI {
    private final MemoryServiceI memoryService;
    private final StorageTextMapper storageTextMapper;

    @Override
    public void deleteByMemoryId(String memoryId) {
        lambdaUpdate().eq(StorageText::getMemoryId, memoryId).remove();
    }

    @Override
    public List<StorageTextVO> getTextByMemoryId(String memoryId) {
        Long userId = UserContext.getUser().getUserId();
        Memory memory = memoryService.getMemoryByMemoryId(memoryId);
        if (!memory.getUserId().equals(userId)){
            throw new RuntimeException("对话记录不属于该用户！");
        }
        List<StorageText> texts = lambdaQuery().eq(StorageText::getMemoryId, memoryId).list();
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }
        List<StorageTextVO> results = new ArrayList<>();
        for (StorageText text : texts){
            StorageTextVO storageTextVO = new StorageTextVO();
            StorageTextVO storageTextVO1 = new StorageTextVO();
            String s = text.getText();
            Map map = JSONUtil.toBean(s, Map.class);
            Object type = map.get("type");
            storageTextVO.setType(type.toString());
            if (type.equals("USER")){
                String contents = map.get("contents").toString();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> result = objectMapper.readValue(
                            contents,
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    storageTextVO.setContentsType(result.get(0).get("type").toString());
                    storageTextVO.setText(result.get(0).get("text").toString());
                    if (result.size() > 1){
                        storageTextVO1.setContentsType(result.get(1).get("type").toString());
                        if (result.get(1).get("type").equals("IMAGE")){
                            Map map1 = BeanUtil.beanToMap(result.get(1).get("image"));
                            storageTextVO1.setText(map1.get("base64Data").toString());
                        }
                    }else{
                        storageTextVO1 = null;
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }else{
                storageTextVO1 = null;
                storageTextVO.setText(map.get("text").toString());
            }
            if (storageTextVO1 != null){
                storageTextVO1.setType(type.toString());
                storageTextVO1.setCreateTime(text.getCreateTime());
                results.add(storageTextVO1);
            }
            storageTextVO.setCreateTime(text.getCreateTime());
            results.add(storageTextVO);
        }
        return results;
    }

    @Override
    public List<StorageTextVO> getHistoryStorage(StorageQuery query) {
        Long userId = UserContext.getUser().getUserId();
        if (query.getMemoryId() == null){
            throw new RuntimeException("对话记录id为空！");
        }
        Memory memory = memoryService.getMemoryByMemoryId(query.getMemoryId());
        if (!memory.getUserId().equals(userId)){
            throw new RuntimeException("对话记录不属于该用户！");
        }
        List<StorageText> texts;
        List<StorageTextVO> results = new ArrayList<>();
        if (query.getLastId() == null){
            Page<StorageText> page = lambdaQuery()
                    .eq(StorageText::getMemoryId, query.getMemoryId())
                    .page(query.toMpPage("create_time", false));
            texts = page.getRecords();
        }else{
            texts = storageTextMapper.findStorageBeforeId(query.getLastId(), query.getPageSize() , query.getMemoryId());
        }
        for (StorageText text : texts){
            StorageTextVO storageTextVO = new StorageTextVO();
            StorageTextVO storageTextVO1 = new StorageTextVO();
            String s = text.getText();
            Map map = JSONUtil.toBean(s, Map.class);
            Object type = map.get("type");
            storageTextVO.setId(text.getId());
            storageTextVO.setType(type.toString());
            if (type.equals("USER")){
                String contents = map.get("contents").toString();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> result = objectMapper.readValue(
                            contents,
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    storageTextVO.setContentsType(result.get(0).get("type").toString());
                    storageTextVO.setText(result.get(0).get("text").toString());
                    if (result.size() > 1){
                        storageTextVO1.setContentsType(result.get(1).get("type").toString());
                        if (result.get(1).get("type").equals("IMAGE")){
                            Map map1 = BeanUtil.beanToMap(result.get(1).get("image"));
                            storageTextVO1.setText(map1.get("base64Data").toString());
                        }
                    }else{
                        storageTextVO1 = null;
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }else{
                storageTextVO1 = null;
                storageTextVO.setText(map.get("text").toString());
            }
            if (storageTextVO1 != null){
                storageTextVO1.setId(text.getId());
                storageTextVO1.setType(type.toString());
                storageTextVO1.setCreateTime(text.getCreateTime());
                results.add(storageTextVO1);
            }
            storageTextVO.setCreateTime(text.getCreateTime());
            results.add(storageTextVO);
        }
        results.sort(Comparator.comparing(StorageTextVO::getId).reversed());
        return results;
    }


}
