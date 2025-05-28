package cn.edu.zust.se.domain.dto;

import lombok.Data;

@Data
public class ContactStatusDTO {
    private Long userId;
    private Long contactId;
    private Integer status;
}
