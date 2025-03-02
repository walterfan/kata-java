package com.github.walterfan.kata.reminder.persistence;

import java.util.Optional;

import com.github.walterfan.kata.reminder.domain.Book;

public interface BookRepositoryV1 {
    Iterable<Book> findAll();
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    Book save(Book book);

    void deleteByIsbn(String isbn);
}
