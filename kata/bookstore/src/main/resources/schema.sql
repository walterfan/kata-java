CREATE DATABASE library;

USE library;

CREATE TABLE Book (
                      isbn VARCHAR(13) PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      borrow_date DATE,
                      return_date DATE
);
