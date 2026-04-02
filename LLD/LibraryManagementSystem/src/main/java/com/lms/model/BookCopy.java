package com.lms.model;

import com.lms.state.AvailableState;
import com.lms.state.ItemState;

/** Represents a physical copy of a library item with state management */
public class BookCopy {
    private final LibraryItem libraryItem;
    private final String copyId;
    private ItemState currentState;

    /** Creates a copy linked to a library item, initially available */
    public BookCopy(String copyId, LibraryItem libraryItem) {
        this.copyId = copyId;
        this.libraryItem = libraryItem;
        this.currentState = new AvailableState();
    }

    /** Returns the parent library item */
    public LibraryItem getLibraryItem() {
        return libraryItem;
    }

    /** Returns this copy's unique ID */
    public String getCopyId() {
        return copyId;
    }

    /** Returns true if copy is in available state */
    public boolean isAvailable(){
        return this.currentState instanceof AvailableState;
    }

//    public ItemState getCurrentState() {
//        return currentState;
//    }

    /** Sets the current state (State pattern) */
    public void setCurrentState(ItemState currentState) {
        this.currentState = currentState;
    }

    /** Delegates checkout to current state */
    public void checkout(Member member){this.currentState.checkout(this,member);}

    /** Delegates return to current state */
    public void returnItem(){ this.currentState.returnCopy(this);}

    /** Delegates hold placement to current state */
    public void placeHold(Member member) { currentState.placeHold(this, member); }

    @Override
    public String toString() {
        return "BookCopy of" +
                "libraryItem=" + libraryItem +
                ", copyId='" + copyId + '\'' +
                '}';
    }
}
