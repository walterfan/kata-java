package com.github.walterfan.bjava.reminder.domain;
/**
 * Spring Boot 3.x or later uses the Jakarta EE namespace (jakarta.validation.constraints)
 * instead of the older Java EE namespace (javax.validation.constraints).
 */

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Book(
        @NotBlank(message = "The book ISBN must be defined.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
        String isbn,
        @NotBlank(message = "The book title must be defined.")
        String title,
        @NotBlank(message = "The book author must be defined.")
        String author,

        Date borrowTime,
        Date returnTime
) {

        public Book(String isbn, String title, String author) {
                this(isbn, title, author, new Date(), null);
        }

        public Book(String isbn, String title, String author, Date borrowTime) {
                this(isbn, title, author, borrowTime, null);
        }
}
