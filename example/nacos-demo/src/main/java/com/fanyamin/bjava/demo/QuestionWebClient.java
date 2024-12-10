package com.fanyamin.bjava.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class QuestionWebClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionWebClient.class);

    WebClient client = WebClient.create("http://localhost:8080");

    public void consume() {

        Mono<Question> questionMono = client.get()
                .uri("/questions/{id}", "1")
                .retrieve()
                .bodyToMono(Question.class);

        questionMono.subscribe(question -> LOGGER.info("Question: {}", question));

        Flux<Question> questionFlux = client.get()
                .uri("/questions")
                .retrieve()
                .bodyToFlux(Question.class);

        questionFlux.subscribe(question -> LOGGER.info("Question: {}", question));
    }
}

