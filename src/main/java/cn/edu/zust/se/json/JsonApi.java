package cn.edu.zust.se.json;

import cn.edu.zust.se.config.LLMConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 结构化输出能力
 */
@RequestMapping("/json")
@RestController
@RequiredArgsConstructor
public class JsonApi {
    private final LLMConfig LLMConfig;

    @RequestMapping("/low/chat")
    public String lowChat(@RequestParam(value = "message")String message) {
        ChatLanguageModel chatLanguageModel = LLMConfig.OllamaChatModel("http://localhost:11434","llama3.1:latest");
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchema.builder()
                        .rootElement(JsonObjectSchema.builder()
                                .addIntegerProperty("age", "the person's age")
                                .addStringProperty("weight", "the person's weight")
                                .required("age", "weight")
                                .build())
                        .build())
                .build();
        ChatResponse chatResponse = chatLanguageModel.chat(ChatRequest.builder()
                .messages(List.of(UserMessage.from(message)))
                .parameters(ChatRequestParameters.builder()
                        .responseFormat(responseFormat)
                        .build())
                .build());
        return chatResponse.aiMessage().text();
    }

    @GetMapping("/high/chat")
    public String highChat(@RequestParam(value = "message") String message) throws JsonProcessingException {
        ChatLanguageModel chatLanguageModel = LLMConfig.OllamaChatModel("http://localhost:11434", "qwen2.5:latest");
        PersonService personService = AiServices.create(PersonService.class, chatLanguageModel);
        Person person = personService.extractPerson(message);
        return new ObjectMapper().writeValueAsString(person);
    }
}
