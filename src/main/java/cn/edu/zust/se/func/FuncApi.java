package cn.edu.zust.se.func;

import cn.edu.zust.se.config.LLMConfig;
import cn.edu.zust.se.util.JsonUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Function calling能力
 */
@RestController
@RequestMapping("/func")
@RequiredArgsConstructor
public class FuncApi {
//    private final Assistant assistant;
    private final LLMConfig LLMConfig;

    @GetMapping("/low/chat")
    public String lowChat(@RequestParam(value = "message") String message){
        ToolSpecification spec = ToolSpecification.builder()
                .name("Calculator")
                .description("输入两个数，对这两个数求和")
                .parameters(JsonObjectSchema.builder()
                        .addIntegerProperty("a", "第一个数")
                        .addIntegerProperty("b", "第二个数")
                        .required("a","b")
                        .build())
                .build();
        ChatLanguageModel chatLanguageModel = LLMConfig.OllamaChatModel("http://localhost:11434","llama3.1:latest");
        ChatResponse chatResponse = chatLanguageModel.doChat(
                ChatRequest.builder()
                        .messages(List.of(UserMessage.from(message)))
                        .parameters(ChatRequestParameters.builder()
                                .toolSpecifications(spec)
                                .build())
                        .build());
        chatResponse.aiMessage().toolExecutionRequests().forEach(toolExecutionRequest -> {
            System.out.println(toolExecutionRequest.name());
            System.out.println(toolExecutionRequest.arguments());
            try {
                Class<?> aClass = Class.forName("cn.edu.zust.se.func." + toolExecutionRequest.name());
                Runnable runnable = (Runnable) JsonUtil.toJsonObject(toolExecutionRequest.arguments(), aClass);
                runnable.run();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return chatResponse.aiMessage().text();
    }

//    @RequestMapping("/high/chat")
//    public String highChat(@RequestParam(value = "message")String message) {
//        return assistant.chat(message);
//    }
}
