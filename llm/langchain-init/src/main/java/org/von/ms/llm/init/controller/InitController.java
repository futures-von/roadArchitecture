package org.von.ms.llm.init.controller;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.von.ms.llm.init.domain.bean.LawPrompt;
import org.von.ms.llm.init.service.*;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Slf4j
@RestController
public class InitController {

    @Autowired
    @Qualifier("qwenPlusModel")
    private ChatModel chatModel;

    @Autowired
    @Qualifier("qwenMaxModel")
    private ChatModel chatMaxModel;

    @Autowired
    @Qualifier("chatAssistantQWEN")
    private ChatAssistant chatAssistant;

    @Autowired
    @Qualifier("chatAssistantQWENStream")
    private ChatAssistant chatAssistantStream;

    @Autowired
    @Qualifier("qwenPlusStreamModel")
    private StreamingChatModel streamingChatModel;

    @Autowired
    @Qualifier("chatMemoryStreamByWindow")
    private ChatMemoryAssistant chatMemoryByWindow;

    @Autowired
    @Qualifier("chatMemoryStreamByToken")
    private ChatMemoryAssistant chatMemoryByToken;

    @Autowired
    private LawChatAssistant lawChatAssistant;

    @Autowired
    private FunctionAssistant functionAssistant;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private QdrantClient qdrantClient;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Value("classpath:static/640.png")
    private Resource rescouce;

    @RequestMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你是谁") String massage) {
        String chat = chatModel.chat(massage);
        log.info("问题: {}", massage);
        log.info("回复: {}", chat);
        return chat;
    }

    @RequestMapping("/highChat")
    public String higtChat(@RequestParam(value = "message", defaultValue = "你是谁") String massage) {
        String chat = chatAssistant.chat(massage);
        log.info("问题: {}", massage);
        log.info("回复: {}", chat);
        return chat;
    }

    @RequestMapping("/imageToTextChat")
    public String imageToTextChat() throws IOException {
        // 获取图片，并转换为base64
        String base64Data = Base64.getEncoder().encodeToString(rescouce.getContentAsByteArray());
        // 构造请求参数
        UserMessage userMessage = UserMessage.from(TextContent.from("请将图片中的内容翻译成中文"),
                ImageContent.from(base64Data, "image/png"));
        // 发送请求
        ChatResponse chatResponse = chatMaxModel.chat(userMessage);
        // 获取请求
        String text = chatResponse.aiMessage().text();

        return text;
    }


    @RequestMapping("/chatStream")
    public Flux<String> chatStream(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        return Flux.create(emitter -> streamingChatModel.chat(message, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String s) {
                        emitter.next(s);
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse chatResponse) {
                        emitter.complete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        emitter.error(throwable);
                    }
                })
        );
    }

    @RequestMapping("/chatStream2")
    public void chatStream2(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        streamingChatModel.chat(message, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String s) {
                System.out.println(s);
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                // 自己组装结果
                System.out.println(chatResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }

    @RequestMapping("/chatStream3")
    public Flux<String> chatStream3(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        return chatAssistantStream.chatStream(message);
    }

    @RequestMapping("/chatMenoryByWindow")
    public Flux<String> chatMenoryByWindow(@RequestParam(value = "userId", defaultValue = "1") Long userId,
                                           @RequestParam(value = "message", defaultValue = "你是谁") String message) {
        return chatMemoryByWindow.chatStreamWithChatMemory(userId, message);
    }

    @RequestMapping("/chatMenoryByToken")
    public Flux<String> chatMenoryByToken(@RequestParam(value = "userId", defaultValue = "1") Long userId,
                                          @RequestParam(value = "message", defaultValue = "你是谁") String message) {
        return chatMemoryByToken.chatStreamWithChatMemory(userId, message);
    }

    @RequestMapping("/chatPrompt/1")
    public String chatPrompt1(@RequestParam(value = "message", defaultValue = "你是谁") String message,
                              @RequestParam(value = "length", defaultValue = "100") int length) {
        String respMsg;
        String chat = lawChatAssistant.chat(message, length);
        respMsg = chat;
        chat = lawChatAssistant.chat("什么是java", length);
        respMsg += chat;
        chat = lawChatAssistant.chat("今天天气怎么样", length);
        respMsg += chat;

        return respMsg;
    }

    @RequestMapping("/chatPrompt/2")
    public String chatPrompt2(@RequestParam(value = "message", defaultValue = "你是谁") String message,
                              @RequestParam(value = "length", defaultValue = "100") int length) {
        LawPrompt prompt = new LawPrompt();
        prompt.setLegal("知识产权");
        prompt.setQuestion("TRIPs协议?");
        String chat = lawChatAssistant.chat(prompt);
        System.out.println(chat);
        return "success : " + LocalDate.now() + "<br> In\n chat: " + chat;
    }

    @RequestMapping("/chatPrompt/3")
    public String chatPrompt3(@RequestParam(value = "message", defaultValue = "你是谁") String message,
                              @RequestParam(value = "length", defaultValue = "100") int length) {
        // 看看源码，默认PromptTemplate构造使用it属性作为默认占位符
        String role = "外科医生";
        String question = "牙疼";
        /*String role="财务会计";
        String question="人民币大写";*/
        //1构造PromptTemplate模板
        PromptTemplate template = PromptTemplate.from("你是一个{{it}}助手,{{question}}怎么办");
        //2由PromptTemplate生成Prompt
        Prompt prompt = template.apply(Map.of("it", role, "question", question));
        //3 Prompt提示词变成UserMessage
        UserMessage userMessage = prompt.toUserMessage();
        // 4 调用大模型
        ChatResponse chatResponse = chatModel.chat(userMessage);
        // 4.1 后台打印
        System.out.println(chatResponse.aiMessage().text());
        // 4.2 前台返回
        return "success : " + LocalDate.now() + "<br> InIn chat: " + chatResponse.aiMessage().text();
    }

    @RequestMapping("/chatFunction")
    public String chatFunction(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        message = "“开张发票。公司：科技有限公司，税号：123456，金额：888";
        String chat = functionAssistant.chat(message);
        return "success : " + LocalDate.now() + "<br> InIn chat: " + chat;
    }

    @RequestMapping("/chatFunctionHigh")
    public String chatFunctionHigh(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        message = message + "“开张发票。公司：科技有限公司，税号：123456，金额：888";
        String chat = functionAssistant.chat(message);
        return "success : " + LocalDate.now() + "<br> InIn chat: " + chat;
    }


    /**
     * 文本向量化测试，看看形成向量后的文本
     * <p>
     * http://localhost:9012/embedding/embed
     *
     * @return
     */
    @GetMapping(value = "/embedding/embed")
    public String embed() {
        String prompt =
                """
                        咏鸡
                        鸡鸣破晓光，
                        红冠映朝阳。
                        金羽披霞彩，
                        昂首步高岗。
                        """;
        Response<Embedding> embeddingResponse = embeddingModel.embed(prompt);
        System.out.println(embeddingResponse);
        return embeddingResponse.content().toString();
    }

    /**
     * 新建向量数据库实例和创建索引：test-gdrant
     * 类似mysql create database test-gdrant
     * http://localhost:9e12/embedding/createCollection
     */
    @GetMapping(value = "/embedding/createCollection")
    public void createCollection() {
        var vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(1024)
                .build();
        qdrantClient.createCollectionAsync("test-qdrant", vectorParams);
    }

    /**
     * 往向量数据库新增文本记录
     */

    @GetMapping(value = "/embedding/add")
    public String add() {
        String prompt =
                """
                        咏鸡
                        鸡鸣破晓光，
                        红冠映朝阳。
                        金羽披彩霞，
                        昂首步高岗。
                        """;
        TextSegment segment1 = TextSegment.from(prompt);
        segment1.metadata().put("author", "von");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        String result = embeddingStore.add(embedding1, segment1);
        System.out.println(result);
        return result;
    }

    @GetMapping(value="/embedding/query1")
    public void query1(){
        Embedding queryEmbedding = embeddingModel.embed("咏鸡说的是什么").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(embeddingSearchRequest);
        System.out.println(searchResult.matches().get(0).embedded().text());
    }

    @GetMapping(value = " /embedding/query2")
    public void query2(){
        Embedding queryEmbedding = embeddingModel.embed("咏鸡").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .filter(metadataKey("author").isEqualTo("zzyy2"))
                .maxResults(1)
                .build();
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(embeddingSearchRequest);
        System.out.println(searchResult.matches().get(0).embedded().text());
    }

    // http://localhost:9013/rag/add
    @GetMapping(value = "/rag/add")
    public String testAdd() throws FileNotFoundException {
        // Document document = FileSystemDocumentLoader.loadDocument("D:I144\Ialibaba-java.docx");
        FileInputStream fileInputStream = new FileInputStream("D:\\44\\alibaba-java.docx");
        Document document = new ApacheTikaDocumentParser().parse(fileInputStream);
        EmbeddingStoreIngestor.ingest(document, embeddingStore);
        String result=chatAssistant.chat("错误码00000和A0001分别是什么");
        System.out.println(result);
        return result;
    }

    /**
     * 第1步，如何进行mcp编码
     * https://docs.4.langchain4j.dev/tutorials/mcp#creating-an-mcp-tool-provider
     * 第2步，如何使用baidumap-mcp，它提供了哪些功能对外服务
     * https://mcp.so/zh/server/baidu-map/baidu-maps?tab=tools
     *
     * 调用api
     * http://localhost:9014/mcp/chat?question=查询61.149.121.66归属地
     * http://localhost:9014/mcp/chat?question=查询北京天气
     * http://localhost:9014/mcp/chat?question=查询昌平到天安门路线规划
     *
     * @return
     * @throws FileNotFoundException
     */

    @GetMapping(value = "/mcp/chat")
    public Flux<String> mcpChat(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        // 1. 构建McpTransport协议
        // 1.1 cmd：启动Windows命令行解释器。
        // 1.2 /c：告诉cmd执行完后面的命令后关闭自身。
        // 1.3 npx:npx = npm execute package，Node.js 的一个工具，用于执行npm包中的可执行文件。
        // 1.4 -y或--yes：自动确认操作（类似于默认接受所有提示）。
        // 1.5 @baidumap/mcp-server-baidu-map:要通过 npx执行的 npm包名
        // 1.6 BAIDU_MAP_API_KEY是访问百度地图开放平台API的AK
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("cmd", "-y", "@baidumap/mcp-server-baidu-map"))
                .environment(Map.of("BAIDU_MAP_API_KEY", System.getenv("BAIDU_MAP_API_KEY")))
                .logEvents(true)
                .build();

        // 2. 构建MCP客户端
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("MyMCPClient")
                .transport(transport)
                .build();

        // 3. 获取工具列表，创建工具集和原生的FunctionCalling类似
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        // 4. 通过AiServivces给我们自定义接口McpService构建实现类并将工具集和大模型赋值给AiService
        MCPService mcpService = AiServices.builder(MCPService.class)
                . streamingChatModel(streamingChatModel)
                .toolProvider(toolProvider)
                .build();

        // 5. 调用我们定义的HighApi接口,通过大模型对百度mcpserver调用
        return mcpService.chatStream(message);
    }
}
