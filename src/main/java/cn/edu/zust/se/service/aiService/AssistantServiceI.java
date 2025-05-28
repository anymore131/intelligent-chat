package cn.edu.zust.se.service.aiService;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface AssistantServiceI {
    Assistant ragInit(boolean deep,boolean web,Long ragId);
    Assistant init(boolean deep,boolean web);
    Flux<String> multimodality(String memoryId, String message, MultipartFile file) throws IOException;
    String chatHelp(Integer number, Long receiverId);
}
