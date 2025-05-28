package cn.edu.zust.se.domain.query;

import cn.edu.zust.se.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageQuery extends PageQuery {
    private Long lastId;
    private String memoryId;
}
