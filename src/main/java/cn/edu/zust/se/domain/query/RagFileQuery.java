package cn.edu.zust.se.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RagFileQuery extends FileQuery{
    private Long ragId;
    private String fileName;
}
