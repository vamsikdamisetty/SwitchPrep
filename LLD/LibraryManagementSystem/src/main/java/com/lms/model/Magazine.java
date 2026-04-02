package com.lms.model;


/** Represents a magazine item in the library */
public class Magazine extends LibraryItem{
    private String publisher;

    /** Creates a magazine with id, title, and publisher */
    public Magazine(String id, String title,String publisher) {
        super(id, title);
        this.publisher = publisher;
    }

    /** Returns the magazine's publisher */
    @Override
    public String getAuthororPublisher() {
        return this.publisher;
    }
}
