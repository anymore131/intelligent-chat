package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageQuery extends PageQuery {
    private Long lastId;     // 当前最后一条消息的ID（用于滚动分页）
    private Long senderId;
    private Long receiverId;
}
