package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.query.StorageQuery;
import cn.edu.zust.se.domain.vo.StorageTextVO;
import cn.edu.zust.se.domain.po.StorageText;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface StorageTextServiceI extends IService<StorageText> {
    void deleteByMemoryId(String memoryId);
    List<StorageTextVO> getTextByMemoryId(String memoryId);
    List<StorageTextVO> getHistoryStorage(StorageQuery query);
}