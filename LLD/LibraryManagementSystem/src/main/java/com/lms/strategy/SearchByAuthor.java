package com.lms.strategy;

import com.lms.model.LibraryItem;

import java.util.List;
import java.util.stream.Collectors;

public class SearchByAuthor implements SearchStrategy{
    @Override
    public List<LibraryItem> search(String term, List<LibraryItem> catalog) {
        return catalog.stream().filter(item -> item.getAuthororPublisher().toLowerCase().contains(term.toLowerCase())).collect(Collectors.toList());
    }
}
