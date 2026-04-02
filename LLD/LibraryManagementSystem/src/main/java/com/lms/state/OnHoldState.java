package com.lms.state;


import com.lms.model.BookCopy;
import com.lms.model.Member;
import com.lms.service.TransactionManagement;

/** State when copy is reserved for a member who placed a hold */
public class OnHoldState implements ItemState{

    /** Only the member who placed the hold can checkout */
    @Override
    public void checkout(BookCopy copy, Member member) {
        // Only a member who placed the hold can check it out.
        if (copy.getLibraryItem().isObserver(member)) {
            TransactionManagement.getInstance().createLoan(member,copy);
            copy.getLibraryItem().removeObserver(member); // Remove from waiting list
            copy.setCurrentState(new CheckedOutState());
            System.out.println("Hold fulfilled. " + copy.getCopyId() + " checked out by " + member.getName());
        } else {
            System.out.println("This item is on hold for another member.");
        }
    }

    /** Invalid - item is on hold, not checked out */
    @Override
    public void returnCopy(BookCopy copy) {
        System.out.println("Invalid action. Item is on hold, not checked out.");
    }

    /** Invalid - item already has a hold */
    @Override
    public void placeHold(BookCopy c, Member m) {
        System.out.println("Item is already on hold.");
    }

}
