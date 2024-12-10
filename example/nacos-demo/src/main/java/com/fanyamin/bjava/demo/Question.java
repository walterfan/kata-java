package com.fanyamin.bjava.demo;

import java.util.Objects;

public class Question {
    private String id;
    private String name;

    public Question(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id) && Objects.equals(name, question.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
