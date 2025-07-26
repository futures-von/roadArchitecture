package org.von.ms.llm.init.service;

public interface FunctionAssistant {

    /**
     * 客户指令：出差住宿发票开票工具
     * 开票信息：公司名称xXX
     * 税号序列:XX
     * 开票金额:XXX.00元
     */
    String chat(String message);
}
