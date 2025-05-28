package cn.edu.zust.se.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author anymore131
 * @since 2025-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_chat_list")
public class UserChatList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.NONE)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("contact_id")
    private Long contactId;

    /**
     * 最后消息
     */
    @TableField("last_message")
    private String lastMessage;

    /**
     * 最后消息时间
     */
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    @TableField("unread_count")
    private Integer unreadCount;

    /**
     * 是否置顶（0-否，1-是）
     */
    @TableField("is_pinned")
    private Integer isPinned;

    @TableField("name")
    private String name;

    /**
     * 联系人类型(1:用户 2:群组)
     */
    @TableField("contact_type")
    private Integer contactType;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-否，1-是）
     */
    @TableField("is_delete")
    private Integer isDelete;
}
