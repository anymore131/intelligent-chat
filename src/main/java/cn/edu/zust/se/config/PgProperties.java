package cn.edu.zust.se.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pgvector")
@Data
public class PgProperties {
    private String database;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String table;
}
