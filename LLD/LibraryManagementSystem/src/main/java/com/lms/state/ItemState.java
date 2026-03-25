package com.lms.state;

import com.lms.model.BookCopy;
import com.lms.model.Member;

public interface ItemState {
    void checkout(BookCopy copy,Member member);

    void returnCopy(BookCopy copy);

    void placeHold(BookCopy copy, Member member);
}
