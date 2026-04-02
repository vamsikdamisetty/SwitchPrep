package com.lms.model;

/** Represents a book item in the library */
public class Book extends LibraryItem {

    private final String author;

    /** Creates a book with id, title, and author */
    public Book(String id, String title,String author) {
        super(id, title);
        this.author = author;
    }

    /** Returns the book's author */
    @Override
    public String getAuthororPublisher() {
        return author;
    }
}
