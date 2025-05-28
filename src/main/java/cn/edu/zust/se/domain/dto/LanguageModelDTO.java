package cn.edu.zust.se.domain.dto;

import lombok.Data;

@Data
public class LanguageModelDTO {
    private Long id;
    private String methodName;
    private String platform;
    private String platformName;
    private String apiKey;
    private String modelName;
}
