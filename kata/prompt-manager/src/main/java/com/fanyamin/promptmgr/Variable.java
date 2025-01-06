package com.fanyamin.promptmgr;
import lombok.Data;

@Data
public class Variable {
    private String id; // UUID
    private String promptId;
    private String key;
    private String value;
}