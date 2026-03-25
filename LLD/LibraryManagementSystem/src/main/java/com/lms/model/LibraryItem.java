package com.lms.model;

import java.util.ArrayList;
import java.util.List;

public abstract class LibraryItem {

    private final String id;
    private final String title;

    private List<BookCopy> copies = new ArrayList<>();
    private List<Member> observers;

    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
        observers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void addCopy(BookCopy copy){
        this.copies.add(copy);
    }

    public abstract String getAuthororPublisher();

    public void addObserver(Member memeber){
        observers.add(memeber);
    }

    public void removeObserver(Member memeber){
        observers.remove(memeber);
    }

    public void notifyObservers() {
        System.out.println("Notifying " + observers.size() + " observers for '" + title + "'...");
        // Use a copy to avoid ConcurrentModificationException if observer unsubscribes
        new ArrayList<>(observers).forEach(observer -> observer.update(this));
    }

    public List<BookCopy> getCopies() {
        return copies;
    }

    public boolean hasObservers() { return !observers.isEmpty(); }
    public boolean isObserver(Member member) { return observers.contains(member); }

    public BookCopy getAvailableCopy() {
        return  copies.stream().filter(BookCopy::isAvailable).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "LibraryItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }


}
