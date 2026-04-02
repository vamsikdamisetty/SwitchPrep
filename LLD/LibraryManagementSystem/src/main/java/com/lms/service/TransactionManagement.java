package com.lms.service;

import com.lms.model.BookCopy;
import com.lms.model.Loan;
import com.lms.model.Member;

import java.util.HashMap;
import java.util.Map;

/** Singleton service for managing loan transactions */
public class TransactionManagement {

    private static TransactionManagement INSTANCE;
    private final Map<String, Loan> activeLoans = new HashMap<>(); // Key: BookCopy ID

    /** Private constructor for singleton */
    private  TransactionManagement(){ }

    /** Returns the singleton instance (thread-safe) */
    public static synchronized TransactionManagement getInstance(){
        if(INSTANCE == null ){
            INSTANCE = new TransactionManagement();
        }
        return INSTANCE;
    }

    /** Creates a new loan record for member and copy */
    public void createLoan(Member member, BookCopy copy){
        if (activeLoans.containsKey(copy.getCopyId())) {
            throw new IllegalStateException("This copy is already on loan.");
        }
        Loan loan = new Loan(copy, member);
        activeLoans.put(copy.getCopyId(), loan);
        member.addLoan(loan);
    }

    /** Ends the loan and removes it from member's records */
    public void endLoan(BookCopy copy){
        Loan loan = activeLoans.remove(copy.getCopyId());

        if(loan != null){
            loan.getMember().removeLoan(loan);
        }
    }

}
