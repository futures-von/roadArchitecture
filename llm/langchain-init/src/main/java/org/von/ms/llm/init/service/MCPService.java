package org.von.ms.llm.init.service;

import reactor.core.publisher.Flux;

public interface MCPService {
    Flux<String> chatStream(String prompt);
}
