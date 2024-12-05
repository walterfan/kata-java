package com.github.walterfan.bjava.reminder.web;
import java.text.ParseException;
import com.github.walterfan.bjava.reminder.domain.Book;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {

    public static final String ISO_8601_DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String TEST_DATE_STR = "2024-05-05T09:34:38.963Z";
    @Autowired
    private JacksonTester<Book> json;

    public static Date stringToDate(String dateString) {
        try {
            // Define the date format for parsing the input string
            SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATE_FMT);
            return formatter.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error parsing date string: " + e.getMessage());
            return null;
        }
    }
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        // Define the date format for ISO 8601 format
        SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATE_FMT);
        return formatter.format(date);
    }

    @Test
    void testSerialize() throws Exception {

        var book = new Book("1234567890", "Title", "Author", stringToDate(TEST_DATE_STR));
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
        var content = """
                {
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "borrowTime": "2024-05-05T09:34:38.963Z"
                }
                """;
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book("1234567890", "Title", "Author", stringToDate(TEST_DATE_STR)));
    }

}

