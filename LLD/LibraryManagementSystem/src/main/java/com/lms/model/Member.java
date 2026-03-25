package com.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private final String id;
    private final String name;
    private final List<Loan> loans = new ArrayList<>();

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void update(LibraryItem item){
        System.out.println("NOTIFICATION for " + name + ": The book '" + item.getTitle() + "' you placed a hold on is now available!");
    }

    public String getId() {
        return id;
    }

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

    public void addLoan(Loan loan) { loans.add(loan); }
    public void removeLoan(Loan loan) { loans.remove(loan); }
}
