package com.lms.state;

import com.lms.model.BookCopy;
import com.lms.model.Member;

/** State interface for book copy lifecycle (State pattern) */
public interface ItemState {
    /** Handles checkout request in current state */
    void checkout(BookCopy copy,Member member);

    /** Handles return request in current state */
    void returnCopy(BookCopy copy);

    /** Handles hold request in current state */
    void placeHold(BookCopy copy, Member member);
}
