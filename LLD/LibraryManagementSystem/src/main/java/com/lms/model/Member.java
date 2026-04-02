package com.lms.model;

import java.util.ArrayList;
import java.util.List;

/** Represents a library member (Observer for hold notifications) */
public class Member {
    private final String id;
    private final String name;
    private final List<Loan> loans = new ArrayList<>();

    /** Creates a member with id and name */
    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Called when a held item becomes available (Observer callback) */
    public void update(LibraryItem item){
        System.out.println("NOTIFICATION for " + name + ": The book '" + item.getTitle() + "' you placed a hold on is now available!");
    }

    /** Returns member ID */
    public String getId() {
        return id;
    }

    /** Returns member name */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    /** Adds a loan to member's active loans */
    public void addLoan(Loan loan) { loans.add(loan); }

    /** Removes a loan from member's active loans */
    public void removeLoan(Loan loan) { loans.remove(loan); }
}
