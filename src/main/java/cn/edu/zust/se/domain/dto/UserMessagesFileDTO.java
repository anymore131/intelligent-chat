package cn.edu.zust.se.domain.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class UserMessagesFileDTO {
    private Long id;
    private Long userId;
    private String fileUUID;
    private String fileName;
    private String filePath;
    private LocalDateTime createTime;

    public String toJson() {
        // 自定义 LocalDateTime 的格式化（可选）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = createTime.format(formatter);

        // 使用 FastJson 序列化
        return JSON.toJSONString(this,
                SerializerFeature.PrettyFormat,          // 美化输出（带缩进）
                SerializerFeature.WriteDateUseDateFormat  // 自动格式化日期
        );
    }
}
