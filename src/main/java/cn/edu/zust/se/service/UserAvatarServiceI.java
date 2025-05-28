package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.UserAvatar;
import cn.edu.zust.se.domain.query.AvatarFileQuery;
import cn.edu.zust.se.domain.vo.AvatarFileVO;
import cn.edu.zust.se.domain.vo.UserAvatarVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserAvatarServiceI extends IService<UserAvatar> {
    UserAvatarVO getUsedAvatar(Long userId);
    List<UserAvatarVO> getAvatarList(Long userId);
    void updateAvatar(Long userId,MultipartFile file);
    void changeUsedAvatar(Long id);
    void initAvatar(Long userId);

    PageDTO<AvatarFileVO> adminPageByQuery(AvatarFileQuery query);
}
