package com.lms.model;


public class Magazine extends LibraryItem{
    private String publisher;

    public Magazine(String id, String title,String publisher) {
        super(id, title);
        this.publisher = publisher;
    }


    @Override
    public String getAuthororPublisher() {
        return this.publisher;
    }
}
