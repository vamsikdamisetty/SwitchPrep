package com.lms.model;

import com.lms.state.AvailableState;
import com.lms.state.ItemState;

public class BookCopy {
    private final LibraryItem libraryItem;
    private final String copyId;
    private ItemState currentState;

    public BookCopy(String copyId, LibraryItem libraryItem) {
        this.copyId = copyId;
        this.libraryItem = libraryItem;
        this.currentState = new AvailableState();
    }

    public LibraryItem getLibraryItem() {
        return libraryItem;
    }

    public String getCopyId() {
        return copyId;
    }

    public boolean isAvailable(){
        return this.currentState instanceof AvailableState;
    }

//    public ItemState getCurrentState() {
//        return currentState;
//    }

    public void setCurrentState(ItemState currentState) {
        this.currentState = currentState;
    }
    public void checkout(Member member){this.currentState.checkout(this,member);}

    public void returnItem(){ this.currentState.returnCopy(this);}

    public void placeHold(Member member) { currentState.placeHold(this, member); }

    @Override
    public String toString() {
        return "BookCopy of" +
                "libraryItem=" + libraryItem +
                ", copyId='" + copyId + '\'' +
                '}';
    }
}
