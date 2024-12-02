package com.github.walterfan.bjava.reminder.domain;

import java.util.Date;

public record Book(
    String title,
    String author,
    String isbn,
    Date borrowTime,
    Date returnTime
) {}
