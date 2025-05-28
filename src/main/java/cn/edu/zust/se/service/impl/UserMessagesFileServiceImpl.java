package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.dto.UserMessagesFileDTO;
import cn.edu.zust.se.domain.po.UserMessages;
import cn.edu.zust.se.domain.po.UserMessagesFile;
import cn.edu.zust.se.domain.query.MessageFileQuery;
import cn.edu.zust.se.domain.vo.MessageFileVO;
import cn.edu.zust.se.mapper.UserMessagesFileMapper;
import cn.edu.zust.se.service.UserMessagesFileServiceI;
import cn.edu.zust.se.util.BeanUtils;
import cn.edu.zust.se.util.MinioUtil;
import cn.edu.zust.se.util.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author anymore131
 * @since 2025-05-13
 */
@Service
@RequiredArgsConstructor
public class UserMessagesFileServiceImpl extends ServiceImpl<UserMessagesFileMapper, UserMessagesFile> implements UserMessagesFileServiceI {
    private final MinioUtil minioUtil;

    @Override
    public UserMessagesFileDTO getFile(String fileUUID) {
        UserMessagesFile file = getOne(lambdaQuery().eq(UserMessagesFile::getFileUUID, fileUUID));
        return BeanUtils.copyBean(file, UserMessagesFileDTO.class);
    }

    @Override
    public UserMessagesFileDTO setFile(MultipartFile file) {
        Long userId = UserContext.getUser().getUserId();
        return putFile(file, userId);
    }

    @Override
    public PageDTO<MessageFileVO> adminPageByQuery(MessageFileQuery query) {
        Page<MessageFileVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<MessageFileVO> iPage = baseMapper.adminPageByQuery(page, query);
        if (iPage == null){
            return PageDTO.empty(0L,0L);
        }
        PageDTO<MessageFileVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }

    @Override
    public UserMessagesFileDTO setFile(MultipartFile file, Long userId) {
        return putFile(file, userId);
    }

    private UserMessagesFileDTO putFile(MultipartFile file, Long userId) {
        if (file == null){
            throw new RuntimeException("文件为空！");
        }
        UUID uuid = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();
        if (originalFilename.contains(" ")) {
            throw new RuntimeException("文件名不能有空格！");
        }
        String uuidFileName = LocalDate.now() + "/" + StringUtils.remove(uuid.toString(), '-')
                + originalFilename.substring(originalFilename.lastIndexOf("."));
        minioUtil.uploadWithUUID(uuidFileName, file);
        String preview = minioUtil.preview(uuidFileName, file.getOriginalFilename());
        int index = preview.indexOf("?");
        String filePath = preview.substring(0, index);
        UserMessagesFile userMessagesFile = new UserMessagesFile();
        userMessagesFile.setUserId(userId);
        userMessagesFile.setFileName(originalFilename);
        userMessagesFile.setFilePath(filePath);
        userMessagesFile.setFileUUID(uuidFileName);
        userMessagesFile.setCreateTime(LocalDateTime.now());
        save(userMessagesFile);
        return BeanUtils.copyBean(userMessagesFile, UserMessagesFileDTO.class);
    }
}
