package com.github.walterfan.bjava.reminder.web;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.github.walterfan.kata.reminder.domain.Book;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static com.github.walterfan.kata.reminder.util.DateUtil.isoStringToInstant;
import static com.github.walterfan.kata.reminder.util.DateUtil.stringToDate;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {


    private static final String TEST_DATE_STR = "2024-05-05T09:34:38.963Z";
    @Autowired
    private JacksonTester<Book> json;



    @Test
    void testSerialize() throws Exception {
        var now = Instant.now();
        var book = new Book(1L,"1234567890", "Title", "Author", 30.0,  isoStringToInstant(TEST_DATE_STR), null, now, now, 10);
        var jsonContent = json.write(book);
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathStringValue("@.borrowTime")
                .isEqualTo(TEST_DATE_STR);
    }

    @Test
    void testDeserialize() throws Exception {
        var instant = Instant.parse(TEST_DATE_STR);
        var content = """
                {
                    "id": 9521,
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "price": 30.0,
                    "borrowTime": "2024-05-05T09:34:38.963Z",
                    "returnTime": "2024-05-05T09:34:38.963Z",
                    "createdDate": "2024-05-05T09:34:38.963Z",
                    "lastModifiedDate": "2024-05-05T09:34:38.963Z",
                    "version": 11
                }
                """;
        var theDate = isoStringToInstant(TEST_DATE_STR);
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book(9521L,"1234567890", "Title", "Author", 30.0,
                        theDate, theDate, theDate, theDate, 11));
    }

}

