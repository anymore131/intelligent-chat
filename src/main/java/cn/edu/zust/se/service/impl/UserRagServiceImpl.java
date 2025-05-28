package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.config.PgProperties;
import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.UserRagDTO;
import cn.edu.zust.se.domain.po.UserRag;
import cn.edu.zust.se.domain.po.RagFile;
import cn.edu.zust.se.domain.query.UserRagQuery;
import cn.edu.zust.se.domain.vo.UserRagVO;
import cn.edu.zust.se.mapper.UserRagMapper;
import cn.edu.zust.se.service.RagFileServiceI;
import cn.edu.zust.se.service.UserRagServiceI;
import cn.edu.zust.se.util.UserContext;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author anymore131
 * @since 2025-03-23
 */
@Service
@RequiredArgsConstructor
public class UserRagServiceImpl extends ServiceImpl<UserRagMapper, UserRag> implements UserRagServiceI {
    private final PgProperties pgProperties;
    private final RagFileServiceI ragFileService;
    private final UserRagMapper userRagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean checkRag(Long ragId) {
        Long userId = UserContext.getUser().getUserId();
        if (userId == null || ragId == null) {
            return false;
        }
        UserRag userRag = lambdaQuery().eq(UserRag::getId, ragId).one();
        if (userRag == null){
            return false;
        }
        return userId.equals(userRag.getUserId());
    }

    @Override
    @Transactional
    public UserRagDTO createRag(String name) {
        Long userId = UserContext.getUser().getUserId();
        UserRag userRag = new UserRag();
        userRag.setName(name);
        userRag.setCreateTime(LocalDateTime.now());
        userRag.setUserId(userId);
        save(userRag);
        redisTemplate.opsForValue().set("daily_count:normal:rag",(Integer)redisTemplate.opsForValue().get("daily_count:normal:rag") + 1);
        return BeanUtil.copyProperties(userRag, UserRagDTO.class);
    }

    @Override
    public void createRag(UserRag userRag) {
        if (userRag.getCreateTime() == null){
            userRag.setCreateTime(LocalDateTime.now());
        }
        save(userRag);
        redisTemplate.opsForValue().set("daily_count:normal:rag",(Integer)redisTemplate.opsForValue().get("daily_count:normal:rag") + 1);
    }

    @Override
    public List<UserRagDTO> getUserRagList() {
        Long userId = UserContext.getUser().getUserId();
        List<UserRag> userRags = lambdaQuery().eq(UserRag::getUserId, userId).list();
        if (userRags != null && !userRags.isEmpty()) {
            return BeanUtil.copyToList(userRags, UserRagDTO.class);
        }
        return List.of();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserRag userRag = getById(id);
        if (userRag == null){
            throw new RuntimeException("该知识库不存在！");
        }
        Long userId = UserContext.getUser().getUserId();
        Integer admin = UserContext.getUser().getAdmin();
        if (!userRag.getUserId().equals(userId) && admin == 1){
            throw new RuntimeException("该知识库不属于你或者权限不足！");
        }
        removeById(id);
        ragFileService.lambdaUpdate().eq(RagFile::getRagId, id).remove();
        String url = "jdbc:postgresql://" + pgProperties.getHost() + ":" + pgProperties.getPort() + "/" + pgProperties.getDatabase();
        String tableName = pgProperties.getTable() + "_" + id;
        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;
        try (
                Connection conn = DriverManager.getConnection(url, pgProperties.getUser(), pgProperties.getPassword());
                Statement stmt = conn.createStatement()
        ) {
            String checkTableSQL = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = '" + tableName.toLowerCase() + "')";
            ResultSet rs = stmt.executeQuery(checkTableSQL);
            if (rs.next() && rs.getBoolean(1)) {
                stmt.executeUpdate(dropTableSQL);
            }
            redisTemplate.opsForValue().set("daily_count:normal:rag",(Integer)redisTemplate.opsForValue().get("daily_count:normal:rag") - 1);
        } catch (SQLException e) {
            throw new RuntimeException("删除数据库失败！");
        }
    }

    @Override
    public void updateRagName(Long id, String name) {
        Long userId = UserContext.getUser().getUserId();
        Integer admin = UserContext.getUser().getAdmin();
        UserRag rag = lambdaQuery().eq(UserRag::getId, id).one();
        if (rag == null){
            throw new RuntimeException("该知识库不存在！");
        }
        if (!rag.getUserId().equals(userId) && admin == 1){
            throw new RuntimeException("该知识库不属于你或者权限不足！");
        }
        lambdaUpdate().eq(UserRag::getId, id).set(UserRag::getName, name).update();
    }

    @Override
    @Transactional
    public void updateRag(UserRag userRag) {
        Long userId = UserContext.getUser().getUserId();
        Integer admin = UserContext.getUser().getAdmin();
        UserRag rag = lambdaQuery().eq(UserRag::getId, userRag.getId()).one();
        if (rag == null){
            throw new RuntimeException("该知识库不存在！");
        }
        if (!rag.getUserId().equals(userId) && admin == 1){
            throw new RuntimeException("该知识库不属于你或者权限不足！");
        }
        rag.setUserId(userRag.getUserId());
        rag.setCreateTime(userRag.getCreateTime());
        rag.setName(userRag.getName());
        UpdateWrapper<UserRag> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userRag.getId());
        update(rag, updateWrapper);
    }

    @Override
    public Long countUserRag() {
        return lambdaQuery().count();
    }

    @Override
    public PageDTO<UserRagVO> pageByQuery(UserRagQuery query) {
        Page<UserRagVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<UserRagVO> iPage = userRagMapper.pageByQuery(query,  page);
        if (iPage == null){
            return new PageDTO<>(0L,0L, null);
        }
        PageDTO<UserRagVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }
}
