package com.github.walterfan.kata.reminder.demo;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.walterfan.kata.reminder.domain.Book;
import com.github.walterfan.kata.reminder.domain.BookRepository;
import com.github.walterfan.kata.reminder.util.DateUtil;

@Component
@Profile("testdata")
public class BookDataLoader {
    
    private final BookRepository bookRepository;
    
    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData() {
        bookRepository.deleteAll();
        var book1 = Book.of("1234567891", "Think in C++", "Bruce Eckel", 50.0, DateUtil.isoStringToInstant("2024-05-01T09:34:38.963Z"), null);
        var book2 = Book.of("1234567892", "Think in Java", "Bruce Eckel",60.0, DateUtil.isoStringToInstant("2024-10-01T09:34:38.963Z"),null);
        //bookRepository.save(book1);
        //bookRepository.save(book2);
        bookRepository.saveAll(List.of(book1, book2)
        );
    }
    
}
