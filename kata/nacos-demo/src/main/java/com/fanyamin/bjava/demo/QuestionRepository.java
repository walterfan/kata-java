package com.fanyamin.bjava.demo;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Repository
public class QuestionRepository {

    private static final Map<String, Question> QUESTION_DATA;

    static {
        QUESTION_DATA = new HashMap<>();
        QUESTION_DATA.put("1", new Question("1", "Question 1"));
        QUESTION_DATA.put("2", new Question("2", "Question 2"));
        QUESTION_DATA.put("3", new Question("3", "Question 3"));
        QUESTION_DATA.put("4", new Question("4", "Question 4"));
        QUESTION_DATA.put("5", new Question("5", "Question 5"));
        QUESTION_DATA.put("6", new Question("6", "Question 6"));
        QUESTION_DATA.put("7", new Question("7", "Question 7"));
        QUESTION_DATA.put("8", new Question("8", "Question 8"));
        QUESTION_DATA.put("9", new Question("9", "Question 9"));
        QUESTION_DATA.put("10", new Question("10", "Question 10"));
    }

    public Mono<Question> findQuestionById(String id) {
        return Mono.just(QUESTION_DATA.get(id));
    }

    public Flux<Question> findAllQuestions() {
        return Flux.fromIterable(QUESTION_DATA.values());
    }

    public Mono<Question> updateQuestion(Question employee) {
        Question existingQuestion = QUESTION_DATA.get(employee.getId());
        if (existingQuestion != null) {
            existingQuestion.setName(employee.getName());
        }
        return Mono.just(existingQuestion);
    }
}
