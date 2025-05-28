package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.po.LanguagePlatform;
import cn.edu.zust.se.domain.vo.LanguagePlatformVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-26
 */
public interface LanguagePlatformServiceI extends IService<LanguagePlatform> {
    List<LanguagePlatformVO> getAllLanguagePlatform();
    List<LanguagePlatformVO> getLanguagePlatformsByUserId(Long userId);
    LanguagePlatformVO createPlatform(LanguagePlatform languagePlatform);
    LanguagePlatformVO updatePlatform(LanguagePlatform languagePlatform);
    void deleteById(Long id);
}
