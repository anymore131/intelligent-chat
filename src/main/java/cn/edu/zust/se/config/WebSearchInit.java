package cn.edu.zust.se.config;

import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 大模型联网能力
 */
@Configuration
public class WebSearchInit {
    @Value("${search.apikey}")
    private String apikey2;
    @Value("${search.engine}")
    private String engine;

    @Value("${web.search.apikey}")
    private String apiKey;

    @Bean
    public SearchApiWebSearchEngine initWebSearchEngine(){
        return SearchApiWebSearchEngine.builder()
                .apiKey(apikey2)
                .engine(engine)
                .build();
    }

    @Bean
    public TavilyWebSearchEngine initTavilyWebSearchTool(){
        return TavilyWebSearchEngine.builder()
                .apiKey(apiKey)
                .searchDepth("advanced") // 深度搜索模式
                .includeAnswer(true) // 包含答案摘要
                .build();
    }
}
