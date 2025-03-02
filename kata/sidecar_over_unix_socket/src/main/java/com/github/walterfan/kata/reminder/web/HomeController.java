package com.github.walterfan.kata.reminder.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.walterfan.kata.reminder.config.ReminderProperties;

@RestController
public class HomeController {
    
    private final ReminderProperties reminderProperties;
    
    public HomeController(ReminderProperties reminderProperties) {
        this.reminderProperties = reminderProperties;
    }

    @GetMapping("/")
    public String getGreeting() {
        return reminderProperties.getGreeting();
    }

}
