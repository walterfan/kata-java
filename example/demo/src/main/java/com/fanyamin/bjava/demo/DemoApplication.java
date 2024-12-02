package com.fanyamin.bjava.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	@Bean
	QuestionRepository questionRepository() {
		return new QuestionRepository();
	}


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		QuestionWebClient employeeWebClient = new QuestionWebClient();
		employeeWebClient.consume();
	}

}
