package cn.edu.zust.se.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemoryDTO {
    private Long id;
    private String memoryId;
    private boolean system = false;
    private String name;
    private LocalDateTime createTime;
}
