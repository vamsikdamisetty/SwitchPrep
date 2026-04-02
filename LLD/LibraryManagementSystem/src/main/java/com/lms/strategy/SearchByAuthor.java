package com.lms.strategy;

import com.lms.model.LibraryItem;

import java.util.List;
import java.util.stream.Collectors;

/** Strategy for searching items by author/publisher name */
public class SearchByAuthor implements SearchStrategy{
    /** Returns items where author/publisher contains search term (case-insensitive) */
    @Override
    public List<LibraryItem> search(String term, List<LibraryItem> catalog) {
        return catalog.stream().filter(item -> item.getAuthororPublisher().toLowerCase().contains(term.toLowerCase())).collect(Collectors.toList());
    }
}
