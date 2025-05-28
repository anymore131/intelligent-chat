package cn.edu.zust.se.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageFileQuery extends FileQuery{
    private Long userId;
    private String fileName;
}
