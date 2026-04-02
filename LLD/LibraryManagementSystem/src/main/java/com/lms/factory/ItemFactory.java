package com.lms.factory;

import com.lms.enums.ItemType;
import com.lms.model.Book;
import com.lms.model.LibraryItem;
import com.lms.model.Magazine;

/** Factory for creating library items based on type */
public class ItemFactory {

    /** Creates a Book or Magazine based on ItemType */
    public static LibraryItem createItem(ItemType type, String id, String title, String author) {
        switch (type) {
            case BOOK: return new Book(id, title, author);
            case MAGAZINE: return new Magazine(id, title, author); // Author might be publisher here
            default: throw new IllegalArgumentException("Unknown item type.");
        }
    }
}
