package com.lms.service;

import com.lms.model.BookCopy;
import com.lms.model.Loan;
import com.lms.model.Member;

import java.util.HashMap;
import java.util.Map;

public class TransactionManagement {

    private static TransactionManagement INSTANCE;
    private final Map<String, Loan> activeLoans = new HashMap<>(); // Key: BookCopy ID

    private  TransactionManagement(){ }

    public static synchronized TransactionManagement getInstance(){
        if(INSTANCE == null ){
            INSTANCE = new TransactionManagement();
        }
        return INSTANCE;
    }

    public void createLoan(Member member, BookCopy copy){
        if (activeLoans.containsKey(copy.getCopyId())) {
            throw new IllegalStateException("This copy is already on loan.");
        }
        Loan loan = new Loan(copy, member);
        activeLoans.put(copy.getCopyId(), loan);
        member.addLoan(loan);
    }

    public void endLoan(BookCopy copy){
        Loan loan = activeLoans.remove(copy.getCopyId());

        if(loan != null){
            loan.getMember().removeLoan(loan);
        }
    }

}

