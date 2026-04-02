package com.lms.model;

import java.time.LocalDate;

/** Represents a loan transaction between member and book copy */
public class Loan {
    private final BookCopy copy;
    private final Member member;
    private final LocalDate checkoutDate;

    /** Creates a loan with current date as checkout date */
    public Loan(BookCopy copy, Member member) {
        this.copy = copy;
        this.member = member;
        this.checkoutDate = LocalDate.now();
    }

    /** Returns the loaned copy */
    public BookCopy getCopy() {
        return copy;
    }

    /** Returns the borrowing member */
    public Member getMember() {
        return member;
    }

    @Override
    public String toString() {
        return
//                "Copy=" + copy.getTitle() +
                ", member=" + member +
                ", checkoutDate=" + checkoutDate +
                '}';
    }
}
