package cn.edu.zust.se;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan("cn.edu.zust.se.mapper")
public class IntelligentChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(IntelligentChatApplication.class, args);
	}
}
