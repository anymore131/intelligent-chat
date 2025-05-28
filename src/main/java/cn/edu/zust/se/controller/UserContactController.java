package cn.edu.zust.se.controller;


import cn.edu.zust.se.domain.R;
import cn.edu.zust.se.domain.dto.UserContactDTO;
import cn.edu.zust.se.domain.po.UserContact;
import cn.edu.zust.se.domain.query.ContactQuery;
import cn.edu.zust.se.domain.vo.UserContactVO;
import cn.edu.zust.se.service.UserContactServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-contact")
@RequiredArgsConstructor
public class UserContactController {
    private final UserContactServiceI userContactService;

    @GetMapping("/{id}")
    public R<UserContactVO> getContact(@PathVariable Long id) {
        UserContactVO contact = userContactService.getContact(id);
        return R.ok(contact);
    }

    // 获取联系人列表
    @PostMapping("/list")
    public R<List<UserContactVO>> getContacts(@RequestBody ContactQuery query) {
        List<UserContactVO> contacts = userContactService.getContacts(query);
        return R.ok(contacts);
    }

    // 添加联系人
    @PostMapping("/add")
    public R<String> addContact(@RequestBody UserContact userContact) {
        userContactService.addContact(userContact);
        return R.ok("申请成功");
    }

    // 获取待确认联系人列表
    @GetMapping("/list/confirm")
    public R<List<UserContactVO>> getShouldConfirmContacts() {
        List<UserContactVO> contacts = userContactService.getShouldConfirmContacts();
        return R.ok(contacts);
    }

    // 确认/拒绝联系人
    @PutMapping("/confirm")
    public R<String> confirmContact(@RequestBody UserContactDTO userContact) {
        userContactService.confirmContact(userContact);
        if (userContact.getStatus() == 1){
            return R.ok("确认成功");
        }else if (userContact.getStatus() == 4){
            return R.ok("拒绝成功");
        }
        return R.error("确认失败");
    }

    // 修改联系人
    @PutMapping
    public R<String> updateContact(@RequestParam Long id, @RequestParam String contactName) {
        UserContactDTO userContact = new UserContactDTO();
        userContact.setId(id);
        userContact.setContactName(contactName);
        userContactService.updateContact(userContact);
        return R.ok("修改成功");
    }

    // 删除联系人
    @DeleteMapping
    public R<String> deleteContact(@RequestParam Long id) {
        UserContactDTO userContact = new UserContactDTO();
        userContact.setId(id);
        userContact.setStatus(2);
        userContactService.updateContact(userContact);
        return R.ok("删除成功");
    }
}
