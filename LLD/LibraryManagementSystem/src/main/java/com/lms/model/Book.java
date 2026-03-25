package com.lms.model;

public class Book extends LibraryItem {

    private final String author;

    public Book(String id, String title,String author) {
        super(id, title);
        this.author = author;
    }

    @Override
    public String getAuthororPublisher() {
        return author;
    }
}
