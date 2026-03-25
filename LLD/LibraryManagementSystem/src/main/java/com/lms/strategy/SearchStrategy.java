package com.lms.strategy;

import com.lms.model.LibraryItem;

import java.util.List;

public interface SearchStrategy {
    List<LibraryItem> search(String term,List<LibraryItem> catalog);
}
