package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.po.LanguagePlatform;
import cn.edu.zust.se.domain.vo.LanguagePlatformVO;
import cn.edu.zust.se.mapper.LanguageModelMapper;
import cn.edu.zust.se.mapper.LanguagePlatformMapper;
import cn.edu.zust.se.service.LanguagePlatformServiceI;
import cn.edu.zust.se.service.UserServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author anymore131
 * @since 2025-04-26
 */
@Service
@RequiredArgsConstructor
public class LanguagePlatformServiceImpl extends ServiceImpl<LanguagePlatformMapper, LanguagePlatform> implements LanguagePlatformServiceI {
    private final LanguageModelMapper languageModelMapper;
    private final UserServiceI userService;

    @Override
    public List<LanguagePlatformVO> getAllLanguagePlatform() {
        // todo admin检验
        List<LanguagePlatform> list = list();
        List<LanguagePlatformVO> languagePlatformVOs = BeanUtils.copyList(list, LanguagePlatformVO.class);
        for  (LanguagePlatformVO languagePlatformVO : languagePlatformVOs) {
            languagePlatformVO.setUserName(userService.getUser(languagePlatformVO.getUserId()).getUserName());
        }
        return languagePlatformVOs;
    }

    @Override
    public List<LanguagePlatformVO> getLanguagePlatformsByUserId(Long userId) {
        // todo admin检验
        List<LanguagePlatform> list = lambdaQuery().eq(LanguagePlatform::getUserId, userId).list();
        return BeanUtils.copyList(list, LanguagePlatformVO.class);
    }

    @Override
    public LanguagePlatformVO createPlatform(LanguagePlatform languagePlatform) {
        // todo admin检验
        Long userId = UserContext.getUser().getUserId();
        languagePlatform.setUserId(userId);
        languagePlatform.setCreateTime(LocalDateTime.now());
        save(languagePlatform);
        return BeanUtil.copyProperties(languagePlatform, LanguagePlatformVO.class);
    }

    @Override
    public LanguagePlatformVO updatePlatform(LanguagePlatform languagePlatform) {
        // todo admin检验
        Long userId = UserContext.getUser().getUserId();
        if (!Objects.equals(languagePlatform.getUserId(), userId)){
            throw new RuntimeException("无法修改！");
        }
        lambdaUpdate()
                .eq(LanguagePlatform::getId, languagePlatform.getId())
                .update(languagePlatform);
        return BeanUtil.copyProperties(languagePlatform, LanguagePlatformVO.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // todo admin检验
        Long userId = UserContext.getUser().getUserId();
        LanguagePlatform languagePlatform = getById(id);
        if (!Objects.equals(languagePlatform.getUserId(), userId)){
            throw new RuntimeException("无法修改！");
        }
        removeById(id);
        Integer count = languageModelMapper.countByPlatformId(id);
        if (count > 0){
            languageModelMapper.deleteByPlatformId(id);
        }
    }
}
