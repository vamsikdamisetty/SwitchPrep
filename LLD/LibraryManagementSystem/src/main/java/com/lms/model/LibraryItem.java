package com.lms.model;

import java.util.ArrayList;
import java.util.List;

/** Abstract base class for library items (Observer subject) */
public abstract class LibraryItem {

    private final String id;
    private final String title;

    private List<BookCopy> copies = new ArrayList<>();
    private List<Member> observers;

    /** Creates a library item with id and title */
    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
        observers = new ArrayList<>();
    }

    /** Returns item ID */
    public String getId() {
        return id;
    }

    /** Returns item title */
    public String getTitle() {
        return title;
    }

    /** Adds a physical copy to this item */
    public void addCopy(BookCopy copy){
        this.copies.add(copy);
    }

    /** Returns author (book) or publisher (magazine) */
    public abstract String getAuthororPublisher();

    /** Adds a member as observer for availability notifications */
    public void addObserver(Member memeber){
        observers.add(memeber);
    }

    /** Removes a member from observers list */
    public void removeObserver(Member memeber){
        observers.remove(memeber);
    }

    /** Notifies all observers that item is available */
    public void notifyObservers() {
        System.out.println("Notifying " + observers.size() + " observers for '" + title + "'...");
        // Use a copy to avoid ConcurrentModificationException if observer unsubscribes
        new ArrayList<>(observers).forEach(observer -> observer.update(this));
    }

    /** Returns all physical copies */
    public List<BookCopy> getCopies() {
        return copies;
    }

    /** Returns true if any observers exist */
    public boolean hasObservers() { return !observers.isEmpty(); }

    /** Returns true if member is an observer */
    public boolean isObserver(Member member) { return observers.contains(member); }

    /** Returns first available copy or null */
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
