package com.lms.strategy;

import com.lms.model.LibraryItem;

import java.util.List;

/** Strategy interface for searching library items */
public interface SearchStrategy {
    /** Searches catalog for items matching the search term */
    List<LibraryItem> search(String term,List<LibraryItem> catalog);
}
