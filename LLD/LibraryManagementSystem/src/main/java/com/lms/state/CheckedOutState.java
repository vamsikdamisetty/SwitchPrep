package com.lms.state;

import com.lms.model.BookCopy;
import com.lms.model.Member;
import com.lms.service.TransactionManagement;

public class CheckedOutState implements ItemState {
    @Override
    public void checkout(BookCopy copy, Member member) {
        System.out.println("Error: Item already under loan");
    }

    @Override
    public void returnCopy(BookCopy copy) {
        TransactionManagement.getInstance().endLoan(copy);
        System.out.println(copy + " returned.");
        if (copy.getLibraryItem().hasObservers()) {
            copy.setCurrentState(new OnHoldState());
            copy.getLibraryItem().notifyObservers(); // Notify members that item is back but on hold
        } else {
            copy.setCurrentState(new AvailableState());
        }
    }

    @Override
    public void placeHold(BookCopy copy, Member member) {
        copy.getLibraryItem().addObserver(member);
        System.out.println(member.getName() + " placed a hold on '" + copy.getLibraryItem().getTitle() + "'");
    }
}
