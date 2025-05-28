package cn.edu.zust.se.service;

import cn.edu.zust.se.domain.dto.UserContactDTO;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.query.ContactQuery;
import cn.edu.zust.se.domain.vo.UserContactVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author anymore131
 * @since 2025-04-12
 */
public interface UserContactServiceI extends IService<UserContact> {
    UserContactVO getContact(Long id);
    UserContact getContactByContactId(Long contactId);
    List<UserContactVO> getContacts(ContactQuery query);
    List<UserContactVO> getShouldConfirmContacts();
    void updateContact(UserContactDTO userContact);
    void addContact(UserContact userContact);
    void confirmContact(UserContactDTO userContact);
    boolean checkContact(Long userId, Long contactId);
}
