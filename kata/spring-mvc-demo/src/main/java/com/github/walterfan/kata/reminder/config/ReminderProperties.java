package com.github.walterfan.kata.reminder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reminder")
public class ReminderProperties {
    
    /**
     * A message to welcome users.
     */
    private String greeting;
    
    public String getGreeting() {
        return greeting;
    }
    
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
    
}