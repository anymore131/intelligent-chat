package cn.edu.zust.se.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author anymore131
 * @since 2025-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("log")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.NONE)
    private Long id;

    @TableField("module")
    private String module;

    @TableField("user_id")
    private Long userId;

    @TableField("operation_type")
    private String operationType;

    @TableField("class_name")
    private String className;

    @TableField("method_name")
    private String methodName;

    @TableField("method_params")
    private String methodParams;

    @TableField("return_value")
    private String returnValue;

    @TableField("error_message")
    private String errorMessage;

    @TableField("operation_ip")
    private String operationIp;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("execution_time")
    private Long executionTime;
}
