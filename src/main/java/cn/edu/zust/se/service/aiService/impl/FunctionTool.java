package cn.edu.zust.se.service.aiService.impl;

import dev.langchain4j.agent.tool.Tool;

public class FunctionTool {

    @Tool("根据给定的聊天记录，使用对我来说合适的口吻和语气，输出适合回复的回答，谢谢！")
    public String chatHelp(Integer number) {
        return "";
    }
}
