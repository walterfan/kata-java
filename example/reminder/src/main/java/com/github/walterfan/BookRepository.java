package com.github.walterfan;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import com.github.walterfan.bjava.reminder.domain.Book;

public interface BookRepository {
    Iterable<Book> findAll();
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    Book save(Book book);

    void deleteByIsbn(String isbn);
}
