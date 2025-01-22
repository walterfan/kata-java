package com.fanyamin.bookstore.model;

import lombok.Data;

import java.util.Date;

@Data
public class Book {
    private String isbn;
    private String title;
    private String author;
    private Date borrowDate;
    private Date returnDate;
}
