package cn.edu.zust.se.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LLMConfig {

    /*
     * Ollama
     */
    public StreamingChatLanguageModel OllamaDeepStreamingChatModel(String api_key, String model_name) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(api_key)
                .modelName(model_name)
                .temperature(0.0)
                .build();
    }
    public ChatLanguageModel OllamaChatModel(String api_key, String model_name) {
        return OllamaChatModel.builder()
                .baseUrl(api_key)
                .modelName(model_name)
                .temperature(0.0)
                .build();
    }
    public EmbeddingModel OllamaEmbeddingModel(String api_key, String model_name) {
        return OllamaEmbeddingModel.builder()
                .baseUrl(api_key)
                .modelName(model_name)
                .build();
    }
    public StreamingChatLanguageModel OllamaMultiModel(String api_key, String model_name) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(api_key)
                .modelName(model_name)
                .build();
    }

    /*
     * Qwen
     */
    public StreamingChatLanguageModel QwenStreamingChatModel(String api_key, String model_name) {
        return QwenStreamingChatModel.builder()
                .apiKey(api_key)
                .modelName(model_name)
                .build();
    }
    public ChatLanguageModel QwenChatModel(String api_key, String model_name) {
        return QwenChatModel.builder()
                .apiKey(api_key)
                .modelName(model_name)
                .build();
    }
    public StreamingChatLanguageModel QwenDeepStreamingChatModel(String api_key, String model_name) {
        return QwenStreamingChatModel.builder()
                .apiKey(api_key)
                .modelName(model_name)
                .build();
    }
    public StreamingChatLanguageModel QwenMultiStreamingChatModel(String api_key, String model_name) {
        return QwenStreamingChatModel.builder()
                .apiKey(api_key)
                .modelName(model_name)
                .build();
    }

    /*
     * ZhipuAi
     */
    public StreamingChatLanguageModel ZhipuAiStreamingChatModel(String api_key, String model_name){
        return ZhipuAiStreamingChatModel.builder()
                .apiKey(api_key)
                .model(model_name)
                .callTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }
    public ChatLanguageModel ZhipuAiChatModel(String api_key, String model_name) {
        return ZhipuAiChatModel.builder()
                .apiKey(api_key)
                .model(model_name)
                .callTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }
    public StreamingChatLanguageModel ZhipuAiDeepStreamingChatModel(String api_key, String model_name){
        return ZhipuAiStreamingChatModel.builder()
                .apiKey(api_key)
                .model(model_name)
                .callTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }
    public StreamingChatLanguageModel ZhipuAiMultiStreamingChatModel(String api_key, String model_name){
        return ZhipuAiStreamingChatModel.builder()
                .apiKey(api_key)
                // 需要设置支持多模态的模型
                .model(model_name)
                .callTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }
}
