package com.fanyamin.bookstore.service;


import com.fanyamin.bookstore.mapper.BookMapper;
import com.fanyamin.bookstore.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public void addBook(Book book) {
        bookMapper.insertBook(book);
    }

    public void updateBook(Book book) {
        bookMapper.updateBook(book);
    }

    public void deleteBook(String isbn) {
        bookMapper.deleteBook(isbn);
    }

    public Book getBookByIsbn(String isbn) {
        return bookMapper.getBookByIsbn(isbn);
    }

    public List<Book> searchBooks(String title) {
        return bookMapper.searchBooks(title);
    }
}