package com.fanyamin.bjava.demo;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping("/{id}")
    public Mono<Question> getQuestionById(@PathVariable String id) {
        return questionRepository.findQuestionById(id);
    }

    @GetMapping
    public Flux<Question> getAllQuestions() {
        return questionRepository.findAllQuestions();
    }

    @PostMapping("/update")
    public Mono<Question> updateQuestion(@RequestBody Question question) {
        return questionRepository.updateQuestion(question);
    }
}
