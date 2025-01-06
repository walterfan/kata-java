package com.fanyamin.promptmgr;

import lombok.Data;
import java.util.Set;
import java.util.Map;

@Data
public class Prompt {
    private String id; // UUID
    private String name;
    private String description;
    private String systemPrompt;
    private String userPrompt;
    private String additionalPrompt;
    private Set<Tag> tags; // Many-to-Many
    private Map<String, String> variables; // One-to-Many
}