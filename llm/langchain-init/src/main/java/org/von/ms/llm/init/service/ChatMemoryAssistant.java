package org.von.ms.llm.init.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface ChatMemoryAssistant {
    /**
     * 聊天带记忆缓存功能
     * @param userId
     * @param prompt
     * @return
     */
    String chatWithChatMemory(@MemoryId Long userId, @UserMessage String prompt);
    Flux<String> chatStreamWithChatMemory(@MemoryId Long userId, @UserMessage String prompt);
}
