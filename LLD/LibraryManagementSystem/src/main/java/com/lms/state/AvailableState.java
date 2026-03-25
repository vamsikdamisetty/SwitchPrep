package com.lms.state;

import com.lms.model.BookCopy;
import com.lms.model.Loan;
import com.lms.model.Member;
import com.lms.service.TransactionManagement;

public class AvailableState implements ItemState{
    @Override
    public void checkout(BookCopy copy, Member member) {
        TransactionManagement.getInstance().createLoan(member,copy);
        copy.setCurrentState(new CheckedOutState());
        System.out.println(copy.getCopyId() + "is checkedOut by :" +  member );
    }

    @Override
    public void returnCopy(BookCopy copy) {
        System.out.println("Invalid:: Item is not under any loan, cannot return");
    }

    @Override
    public void placeHold(BookCopy copy, Member member) {
        System.out.println("Cannot place hold on an available item. Please check it out.");
    }
}
