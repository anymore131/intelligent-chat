package cn.edu.zust.se.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AvatarFileQuery extends FileQuery{
    private Integer userId;
    private Integer status;
}
