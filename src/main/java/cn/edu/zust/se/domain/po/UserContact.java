package cn.edu.zust.se.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author anymore131
 * @since 2025-04-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_contact")
public class UserContact implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("contact_id")
    private Long contactId;

    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人类型(1:用户 2:群组)
     */
    @TableField("contact_type")
    private Integer contactType;

    /**
     * 如果是群组联系人，则记录群组ID
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 关系状态(1:正常 2:已删除 3:待确认 4:已拒绝)
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后联系时间
     */
    @TableField("last_contact_time")
    private LocalDateTime lastContactTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
