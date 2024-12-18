package com.github.walterfan.kata.reminder.domain;
/**
 * Spring Boot 3.x or later uses the Jakarta EE namespace (jakarta.validation.constraints)
 * instead of the older Java EE namespace (javax.validation.constraints).
 */

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

public record Book(
        @Id
        Long id,
        
        @NotBlank(message = "The book ISBN must be defined.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
        String isbn,
        @NotBlank(message = "The book title must be defined.")
        String title,
        @NotBlank(message = "The book author must be defined.")
        String author,
        
        @NotNull(message = "The book price must be defined.")
        @Positive(message = "The book price must be greater than zero.")
        Double price,

        Instant borrowTime,

        Instant returnTime,
        
        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,
        
        @Version
        int version
) {
        
        public static Book of(String isbn, String title, String author, Double price, Instant borrowTime, Instant returnTime) {
                return new Book(null, isbn, title, author, price, borrowTime, returnTime,  null, null, 0);
        }
        
}
