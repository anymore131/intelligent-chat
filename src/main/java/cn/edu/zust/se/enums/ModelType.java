package cn.edu.zust.se.enums;

import lombok.Getter;

@Getter
public enum ModelType {
    CHAT("Chat"),
    DEEP("Deep"),
    EMBEDDING("Embedding"),
    MULTI("Multi"),
    TOOL("Tool");

    private final String type;

    ModelType(String type) {
        this.type = type;
    };
}
