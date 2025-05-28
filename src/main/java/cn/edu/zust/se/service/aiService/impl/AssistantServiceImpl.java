package cn.edu.zust.se.service.aiService.impl;

import cn.edu.zust.se.config.EmbeddingConfig;
import cn.edu.zust.se.domain.dto.LanguageModelDTO;
import cn.edu.zust.se.service.LanguageModelServiceI;
import cn.edu.zust.se.service.UserMessagesServiceI;
import cn.edu.zust.se.service.UserRagServiceI;
import cn.edu.zust.se.service.aiService.Assistant;
import cn.edu.zust.se.service.aiService.AssistantServiceI;
import cn.edu.zust.se.util.JsonUtil;
import cn.edu.zust.se.util.MethodInvoker;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AssistantServiceImpl implements AssistantServiceI {
    private final EmbeddingConfig embeddingConfig;
    private final TavilyWebSearchEngine tavilyWebSearchEngine;
    private final UserRagServiceI userRagService;
    private final PersistentChatMemoryStore store;
    private final LanguageModelServiceI languageModelService;
    private final UserMessagesServiceI userMessagesService;
    private final RedisTemplate<String, Object> redisTemplate;

    public Assistant ragInit(boolean deep, boolean web, Long ragId) {
        chatRedisPlus("rag");

        LanguageModelDTO languageModel = getModel(deep, false);
        StreamingChatLanguageModel model = (StreamingChatLanguageModel) MethodInvoker.invokeMethod(
                "cn.edu.zust.se.config.LLMConfig",
                languageModel.getMethodName(),
                languageModel.getApiKey(),
                languageModel.getModelName()
        );

        if (!userRagService.checkRag(ragId)) {
            throw new RuntimeException("知识库错误！");
        }

        EmbeddingStore<TextSegment> embeddingStore = embeddingConfig.EmbeddingStore(ragId);
        ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .chatMemoryStore(store)
                .maxMessages(10)
                .id(memoryId)
                .build();
        if (web) {
            chatRedisPlus("web");
            return AiServices.builder(Assistant.class)
                    .chatMemoryProvider(memoryProvider)
                    .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                    .tools(new WebSearchTool(tavilyWebSearchEngine))
                    .streamingChatLanguageModel(model)
                    .build();
        } else {
            return AiServices.builder(Assistant.class)
                    .chatMemoryProvider(memoryProvider)
                    .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                    .streamingChatLanguageModel(model)
                    .build();
        }

    }

    public Assistant init(boolean deep, boolean web) {
        LanguageModelDTO languageModel = getModel(deep, false);
        StreamingChatLanguageModel model = (StreamingChatLanguageModel) MethodInvoker.invokeMethod(
                "cn.edu.zust.se.config.LLMConfig",
                languageModel.getMethodName(),
                languageModel.getApiKey(),
                languageModel.getModelName()
        );

        ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .chatMemoryStore(store)
                .maxMessages(10)
                .id(memoryId)
                .build();
        if (web) {
            chatRedisPlus("web");
            return AiServices.builder(Assistant.class)
                    .chatMemoryProvider(memoryProvider)
                    .tools(new WebSearchTool(tavilyWebSearchEngine))
                    .streamingChatLanguageModel(model)
                    .build();
        } else {
            return AiServices.builder(Assistant.class)
                    .chatMemoryProvider(memoryProvider)
                    .streamingChatLanguageModel(model)
                    .build();
        }
    }

    @Override
    public Flux<String> multimodality(String memoryId, String message, MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return Flux.just("请上传正确的文件！");
        }

        LanguageModelDTO languageModel = getModel(false, true);
        StreamingChatLanguageModel streamingChatLanguageModel = (StreamingChatLanguageModel) MethodInvoker.invokeMethod(
                "cn.edu.zust.se.config.LLMConfig",
                languageModel.getMethodName(),
                languageModel.getApiKey(),
                languageModel.getModelName()
        );

        byte[] bytes = file.getBytes();
        UserMessage userMessage = UserMessage.from(
                TextContent.from(message),
                ImageContent.from(Base64.getEncoder().encodeToString(bytes), contentType));
        UserMessage userMessageToKeep = UserMessage.from(
                TextContent.from(message),
                ImageContent.from(file.getOriginalFilename(), contentType));
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();
        ChatRequest chatRequest = ChatRequest
                .builder()
                .messages(userMessage)
                .build();
        chatMemory.add(userMessageToKeep);

        return Flux.create(sink -> {
            streamingChatLanguageModel.chat(chatRequest, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    sink.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    sink.complete();
                    chatMemory.add(completeResponse.aiMessage());
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });
        });
    }

    @Override
    public String chatHelp(Integer number, Long receiverId) {
        String message = userMessagesService.getHistoryString(number, receiverId);
        ToolSpecification spec = ToolSpecification.builder()
                .description("根据给定的聊天记录，使用对我来说合适的口吻和语气，输出适合回复的回答")
                .build();

        List<LanguageModelDTO> list = languageModelService.getModelByType("Tool");
        Random random = new Random();
        LanguageModelDTO languageModel = list.get(random.nextInt(list.size()));
        ChatLanguageModel model = (ChatLanguageModel) MethodInvoker.invokeMethod(
                "cn.edu.zust.se.config.LLMConfig",
                languageModel.getMethodName(),
                languageModel.getApiKey(),
                languageModel.getModelName()
        );
        if (model == null) {
            return "模型加载错误！";
        }

        ChatResponse chatResponse = model.doChat(
                ChatRequest.builder()
                        .messages(List.of(UserMessage.from(message)))
                        .parameters(ChatRequestParameters.builder()
                                .toolSpecifications(spec)
                                .build())
                        .build());

        chatResponse.aiMessage().toolExecutionRequests().forEach(toolExecutionRequest -> {
            try {
                Class<?> aClass = Class.forName("cn.edu.zust.se.service.aiService.FunctionTool");
                Runnable runnable = (Runnable) JsonUtil.toJsonObject(toolExecutionRequest.arguments(), aClass);
                runnable.run();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return chatResponse.aiMessage().text();
    }

    private LanguageModelDTO getModel(boolean deep, boolean multi) {
        List<LanguageModelDTO> list;
        // todo 负载均衡
        if (multi) {
            list = languageModelService.getModelByType("Multi");
            chatRedisPlus("multi");
        } else {
            if (deep) {
                list = languageModelService.getModelByType("Deep");
                chatRedisPlus("deep");
            } else {
                list = languageModelService.getModelByType("Chat");
                chatRedisPlus("chat");
            }
        }
        Random random = new Random();
        LanguageModelDTO languageModel = list.get(random.nextInt(list.size()));
        modelRedisPlus(languageModel);
        return languageModel;
    }

    private void modelRedisPlus(LanguageModelDTO languageModel) {
        boolean exists = redisTemplate.hasKey("daily_count:model:" + languageModel.getPlatformName() + "-" + languageModel.getPlatform() + "-" + languageModel.getModelName());
        if (!exists) {
            redisTemplate.opsForValue().set("daily_count:model:" + languageModel.getPlatformName() + "-" + languageModel.getPlatform() + "-" + languageModel.getModelName(), 1);
        } else {
            redisTemplate.opsForValue().set("daily_count:model:" + languageModel.getPlatformName() + "-" + languageModel.getPlatform() + "-" + languageModel.getModelName(),
                    (Integer) redisTemplate.opsForValue().get("daily_count:model:" + languageModel.getPlatformName() + "-" + languageModel.getPlatform() + "-" + languageModel.getModelName()) + 1);
        }
    }

    private void modelRedisMinus(LanguageModelDTO languageModel) {
        redisTemplate.opsForValue().set("daily_count:model:" + languageModel.getPlatformName() + "-" + languageModel.getPlatform() + "-" + languageModel.getModelName(),
                (Integer) redisTemplate.opsForValue().get("daily_count:model:" + languageModel.getPlatformName() + "-" + languageModel.getPlatform() + "-" + languageModel.getModelName()) - 1);
    }

    private void chatRedisPlus(String type) {
        redisTemplate.opsForValue().set("daily_count:chat:" + type,
                (Integer) redisTemplate.opsForValue().get("daily_count:chat:" + type) + 1);
    }
}
