package com.fanyamin.promptmgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    @GetMapping
    public List<Prompt> getAllPrompts() {
        return promptService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prompt> getPromptById(@PathVariable String id) {
        Optional<Prompt> prompt = promptService.findById(id);
        return prompt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Prompt createPrompt(@RequestBody Prompt prompt) {
        return promptService.save(prompt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prompt> updatePrompt(@PathVariable String id, @RequestBody Prompt promptDetails) {
        Optional<Prompt> prompt = promptService.findById(id);
        if (prompt.isPresent()) {
            Prompt updatedPrompt = prompt.get();
            updatedPrompt.setName(promptDetails.getName());
            updatedPrompt.setDescription(promptDetails.getDescription());
            updatedPrompt.setSystemPrompt(promptDetails.getSystemPrompt());
            updatedPrompt.setUserPrompt(promptDetails.getUserPrompt());
            updatedPrompt.setAdditionalPrompt(promptDetails.getAdditionalPrompt());
            updatedPrompt.setTags(promptDetails.getTags());
            updatedPrompt.setVariables(promptDetails.getVariables());
            return ResponseEntity.ok(promptService.save(updatedPrompt));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrompt(@PathVariable String id) {
        if (promptService.findById(id).isPresent()) {
            promptService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Prompt> searchPrompts(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String description) {
        return promptService.searchByNameOrDescription(name, description);
    }
}