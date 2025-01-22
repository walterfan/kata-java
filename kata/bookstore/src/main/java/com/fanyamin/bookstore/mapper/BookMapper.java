package com.fanyamin.bookstore.mapper;

import com.fanyamin.bookstore.model.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {
    void insertBook(Book book);

    void updateBook(Book book);

    void deleteBook(String isbn);

    Book getBookByIsbn(String isbn);

    List<Book> searchBooks(String title);
}