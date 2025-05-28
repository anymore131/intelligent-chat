package cn.edu.zust.se.service.aiService.impl;

import cn.edu.zust.se.domain.po.StorageText;
import cn.edu.zust.se.service.ChatServiceI;
import cn.edu.zust.se.domain.po.Chat;
import cn.edu.zust.se.service.StorageTextServiceI;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private ChatServiceI chatService;
    @Autowired
    private StorageTextServiceI storageTextService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<Chat> chats = chatService.lambdaQuery().eq(Chat::getMemoryId, memoryId).list();
        if (chats != null && !chats.isEmpty()) {
            return ChatMessageDeserializer.messagesFromJson(chats.get(chats.size()-1).getText());
        }
        return List.of();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Chat chat = new Chat();
        chat.setMemoryId(memoryId.toString());
        chat.setText(ChatMessageSerializer.messagesToJson(messages));
        chat.setCreateTime(LocalDateTime.now());
        chatService.save(chat);
        StorageText storageText = new StorageText();
        storageText.setMemoryId(memoryId.toString());
        storageText.setCreateTime(LocalDateTime.now());
        String text = ChatMessageSerializer.messageToJson(messages.get(messages.size()-1));
        Map map = JSONUtil.toBean(text, Map.class);
        // 排除web搜索的结果
        if (map.get("toolExecutionRequests") != null || map.get("id") != null){
            return;
        }
        storageText.setText(text);
        storageTextService.save(storageText);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        chatService.lambdaUpdate().eq(Chat::getMemoryId, memoryId).remove();
    }
}
