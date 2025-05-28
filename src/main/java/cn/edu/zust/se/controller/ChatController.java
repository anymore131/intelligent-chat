package cn.edu.zust.se.controller;

import cn.edu.zust.se.domain.dto.ChatDTO;
import cn.edu.zust.se.domain.po.SystemMessage;
import cn.edu.zust.se.service.*;
import cn.edu.zust.se.service.aiService.AssistantServiceI;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AssistantServiceI assistantService;
    private final ChatServiceI chatService;
    private final MemoryServiceI memoryService;
    private final SystemMessageServiceI systemMessageService;

    // 流式对话
    @PostMapping(value = "/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> highStream(@RequestBody ChatDTO chatDTO){
        if(!chatService.streamCheck(chatDTO.getMemoryId())){
            return Flux.just("用户对话错误！");
        }
        memoryService.initMemory(chatDTO.getMemoryId(),chatDTO.getMessage());
        SystemMessage systemMessage = systemMessageService.getByMemoryId(chatDTO.getMemoryId());
        if (systemMessage != null){
            return Flux.just("对话方法错误");
        }
        if (chatDTO.getRagId() != null) {
            return assistantService
                    .ragInit(chatDTO.isDeep(), chatDTO.isWeb(),chatDTO.getRagId())
                    .stream(chatDTO.getMemoryId(),chatDTO.getMessage());
        } else {
            return assistantService
                    .init(chatDTO.isDeep(), chatDTO.isWeb())
                    .stream(chatDTO.getMemoryId(),chatDTO.getMessage());
        }
    }

    // 角色流式对话
    @PostMapping(value = "/system/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> systemStream(@RequestBody ChatDTO chatDTO){
        if(!chatService.streamCheck(chatDTO.getMemoryId())){
            return Flux.just("用户对话错误！");
        }
        SystemMessage systemMessage = systemMessageService.getByMemoryId(chatDTO.getMemoryId());
        if (systemMessage == null){
            return Flux.just("对话方法错误");
        }
        return assistantService
                .init(false,false)
                .stream(systemMessage.getDescription(),chatDTO.getMemoryId(),chatDTO.getMessage());
    }

    // 多模态流式对话
    @PostMapping(value = "/multimodality",
            produces = "text/stream;charset=UTF-8",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<String> multimodality(@RequestParam(value = "memoryId") String memoryId,
                                      @RequestParam(value = "message")String message,
                                      @RequestPart("file") MultipartFile file) throws IOException {
        if(!chatService.streamCheck(memoryId)){
            return Flux.just("用户对话错误！");
        }
        memoryService.initMemory(memoryId,message);
        return assistantService.multimodality(memoryId,message,file);
    }

    @GetMapping(value = "/stream/help", produces = "text/stream;charset=UTF-8")
    public String streamHelp(@RequestParam(value = "number") Integer number,
                                   @RequestParam("receiver_id") Long receiverId){
        return assistantService.chatHelp(number, receiverId);
    }
}
