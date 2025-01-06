package com.fanyamin.promptmgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromptService {

    @Autowired
    private PromptMapper promptMapper;

    public List<Prompt> findAll() {
        return promptMapper.findAll();
    }

    public Optional<Prompt> findById(String id) {
        return Optional.ofNullable(promptMapper.findById(id));
    }

    public Prompt save(Prompt prompt) {
        promptMapper.save(prompt);
        return prompt;
    }

    public void deleteById(String id) {
        promptMapper.deleteById(id);
    }

    public List<Prompt> searchByNameOrDescription(String name, String description) {
        return promptMapper.searchByNameOrDescription(name, description);
    }
}