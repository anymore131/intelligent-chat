package cn.edu.zust.se.service.aiService;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;

public interface Assistant {
    String chat(@UserMessage String message);

    Flux<String> stream(@UserMessage String message);

    @SystemMessage("{{systemMessage}}")
    Flux<String> stream(@V("systemMessage")String systemMessage, @MemoryId String memoryId, @UserMessage String message);

    Flux<String> stream(@MemoryId String memoryId,@UserMessage String message);
}

