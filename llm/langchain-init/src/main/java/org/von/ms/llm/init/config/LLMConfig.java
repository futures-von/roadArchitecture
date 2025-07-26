package org.von.ms.llm.init.config;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.von.ms.llm.init.domain.bean.InvoiceHandler;
import org.von.ms.llm.init.domain.bean.RedisChatMemoryStore;
import org.von.ms.llm.init.service.*;

import java.util.List;
import java.util.Map;

@Configuration
public class LLMConfig {

    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Bean("qwenPlusModel")
    public ChatModel chatModelQWEN() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("qwenApiKey"))
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true) // 开启日志的请求,需要日志级别为DEBUG
                .logResponses(true) // 开启日志的响应,需要日志级别为DEBUG
//                .listeners() // 配置监听，类似于aop的环绕通知
//                .maxRetries(3) // 设置重试次数
//                .timeout(Duration.ofSeconds(60)) // 设置请求超时时间
                .build();
    }

    @Bean("qwenMaxModel")
    public ChatModel chatModelQWENMax() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("qwenApiKey"))
                .modelName("qwen-vl-max")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true) // 开启日志的请求,需要日志级别为DEBUG
                .logResponses(true) // 开启日志的响应,需要日志级别为DEBUG
//                .listeners() // 配置监听，类似于aop的环绕通知
//                .maxRetries(3) // 设置重试次数
//                .timeout(Duration.ofSeconds(60)) // 设置请求超时时间
                .build();
    }

    @Bean("qwenPlusStreamModel")
    public StreamingChatModel streamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("qwenApiKey"))
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true) // 开启日志的请求,需要日志级别为DEBUG
                .logResponses(true) // 开启日志的响应,需要日志级别为DEBUG
//                .listeners() // 配置监听，类似于aop的环绕通知
                .build();
    }

    @Bean("qwenLongModel")
    public ChatModel chatLongModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("qwenApiKey"))
                .modelName("qwen-long")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true) // 开启日志的请求,需要日志级别为DEBUG
                .logResponses(true) // 开启日志的响应,需要日志级别为DEBUG
//                .listeners() // 配置监听，类似于aop的环绕通知
                .build();
    }

    @Bean("qwenLongStreamModel")
    public StreamingChatModel chatLongStreamModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("qwenApiKey"))
                .modelName("qwen-long")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true) // 开启日志的请求,需要日志级别为DEBUG
                .logResponses(true) // 开启日志的响应,需要日志级别为DEBUG
//                .listeners() // 配置监听，类似于aop的环绕通知
                .build();
    }

    @Bean
    public ChatAssistant chatAssistantQWEN(@Qualifier("qwenPlusModel") ChatModel chatModelQWEN) {
        return AiServices.create(ChatAssistant.class, chatModelQWEN);
    }

    @Bean
    public ChatAssistant chatAssistantQWENStream(@Qualifier("qwenPlusStreamModel") StreamingChatModel streamingChatModel) {
        return AiServices.create(ChatAssistant.class, streamingChatModel);
    }

    @Bean("chatMemoryByWindow")
    public ChatMemoryAssistant chatMemoryByWindow(@Qualifier("qwenLongModel") ChatModel chatModelQWEN) {
        return AiServices.builder(ChatMemoryAssistant.class)
                .chatModel(chatModelQWEN)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(100))
                .build();
    }

    /**
     * 基于TokenCountEstimator默认的token分词器，需要结合Tokenizer计算ChatMessage的token数量
     * @param chatModelQWEN
     * @return
     */
    @Bean("chatMemoryByToken")
    public ChatMemoryAssistant chatMemoryByToken(@Qualifier("qwenLongModel") ChatModel chatModelQWEN) {
        return AiServices.builder(ChatMemoryAssistant.class)
                .chatModel(chatModelQWEN)
                .chatMemoryProvider(memoryId -> TokenWindowChatMemory.withMaxTokens(1000,
                        new OpenAiTokenCountEstimator("gpt-4")))
                .build();
    }


    /**
     * 基于流式的带会话记忆的语言模型
     * @param streamingChatModel
     * @return
     */
    @Bean("chatMemoryStreamByWindow")
    public ChatMemoryAssistant chatMemoryStreamByWindow(@Qualifier("qwenLongStreamModel") StreamingChatModel streamingChatModel) {
        return AiServices.builder(ChatMemoryAssistant.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(100))
                .build();
    }

    /**
     * 基于TokenCountEstimator默认的token分词器，需要结合Tokenizer计算ChatMessage的token数量
     * @param streamingChatModel
     * @return
     */
    @Bean("chatMemoryStreamByToken")
    public ChatMemoryAssistant chatMemoryStreamByToken(@Qualifier("qwenLongStreamModel") StreamingChatModel streamingChatModel) {
        return AiServices.builder(ChatMemoryAssistant.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> TokenWindowChatMemory.withMaxTokens(1000,
                        new OpenAiTokenCountEstimator("gpt-4")))
                .build();
    }

    @Bean
    public LawChatAssistant lawChatAssistant(@Qualifier("qwenLongModel") ChatModel chatModel) {
        return AiServices.create(LawChatAssistant.class, chatModel);
    }


    /**
     * 基于流式的带会话记忆以及持久化的语言模型
     * @param streamingChatModel
     * @return
     */
    @Bean
    public ChatPersistenceAssistant chatPersistenceAssistant(@Qualifier("qwenLongStreamModel") StreamingChatModel streamingChatModel) {
        return AiServices.builder(ChatPersistenceAssistant.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .chatMemoryStore(redisChatMemoryStore)
                        .maxMessages(1000)
                        .build())
                .build();
    }
    /**
     * @Description:第一组 Low Level Tool API
     * https://docs.langchain4j.dev/tutorials/tools#low-level-tool-api
     */
    @Bean
    public FunctionAssistant functionAssistant(@Qualifier("qwenPlusModel") ChatModel chatModel) {
        //工具说明ToolSpecification
        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("开具发票助手")
                .description("根据用户提交的开票信息，开具发票")
                .parameters(JsonObjectSchema.builder()
                        .addStringProperty("companyName", "公司名称")
                        .addStringProperty("dutyNumber", "税号序列")
                        .addStringProperty("amount", "开票金额，保留两位有效数字")
                        .build())
                .build();
        //业务逻辑ToolExecutor
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            System.out.println(toolExecutionRequest.id());
            System.out.println(toolExecutionRequest.name());
            String arguments1 = toolExecutionRequest.arguments();
            System.out.println("arguments1****》" + arguments1);
            return "开具成功";
        };

        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(Map.of(toolSpecification, toolExecutor))
                .build();
    }

    /**
     * @Description:第二组 High Level Tool API
     * https://docs.langchain4j.dev/tutorials/tools#high-level-tool-api
     */
    @Bean("highFunctionAssistant")
    public FunctionAssistant highFunctionAssistant(@Qualifier("qwenPlusModel") ChatModel chatModel) {
        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(new InvoiceHandler())
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey("qwenApiKey")
                .modelName("text-embedding-v3")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(QdrantGrpcClient
                .newBuilder("127.0.0.1", 6334, false)
                .build());
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return QdrantEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6334)
                .collectionName("test-qdrant")
                .build();
    }

    /**
     * 需要预处理文档并将其存储在专门的嵌入存储（也称为矢量数据库）中。当用户提出问题时，这对于快速找到相关信息是必要的。
     * 我们可以使用我们支持的15多个嵌入存储中的任何一个，但为了简单起见，我们将使用内存中的嵌入存储：
     * https://docs.langchain4j.dev/integrations/embedding-stores/in-memory
     * @return
     */

    @Bean
    public InMemoryEmbeddingStore<TextSegment> inMemoryEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public ChatAssistant ragAssistant(@Qualifier("qwenLongModel") ChatModel chatModel, EmbeddingStore<TextSegment> embeddingStore){
        return AiServices.builder(ChatAssistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }
}
