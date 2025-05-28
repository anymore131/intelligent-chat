package cn.edu.zust.se.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 1. 配置支持 LocalDateTime 的 ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 支持 Java 8 时间类型
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳格式
        mapper.activateDefaultTyping( // 启用类型信息（避免反序列化时类型丢失）
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 2. 创建序列化器
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(mapper); // 直接注入配置好的 ObjectMapper

        // 3. 设置序列化规则
        template.setKeySerializer(new StringRedisSerializer()); // Key 使用字符串序列化
        template.setValueSerializer(valueSerializer); // Value 使用 JSON 序列化
        template.setHashKeySerializer(new StringRedisSerializer()); // Hash Key 同 Key
        template.setHashValueSerializer(valueSerializer); // Hash Value 同 Value

        // 4. 初始化模板
        template.afterPropertiesSet();
        return template;
    }
}
