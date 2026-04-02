package com.lms.service;

import com.lms.enums.ItemType;
import com.lms.factory.ItemFactory;
import com.lms.model.BookCopy;
import com.lms.model.LibraryItem;
import com.lms.model.Member;
import com.lms.strategy.SearchStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Singleton facade for library operations */
public class LibraryManagementSystem {

    private static LibraryManagementSystem instance;

    private Map<String, LibraryItem> catalog = new HashMap<>();
    private Map<String, Member> members = new HashMap<>();
    private Map<String, BookCopy> copies = new HashMap<>();

    /** Private constructor for singleton */
    private LibraryManagementSystem() {

    }

    /** Returns the singleton instance (thread-safe) */
    public synchronized static LibraryManagementSystem getInstance(){
        if(instance == null){
            instance = new LibraryManagementSystem();
        }
        return instance;
    }

    /** Creates and adds a library item with specified copies to the catalog */
    public LibraryItem addItem(ItemType type,String  id, String title, String author, int numCopies){
        LibraryItem item = ItemFactory.createItem(type,id,title,author);
        catalog.put(item.getId(),item);
        for (int i=0;i<numCopies;i++) {
            BookCopy copy = new BookCopy(id + "_" + i, item);
            item.addCopy(copy);
            copies.put(copy.getCopyId(), copy);
        }
        System.out.println("Item" + item + " added to catalog");
        return item;
    }


    /** Registers a new member in the system */
    public Member addMember(String id,String name){
        Member member = new Member(id,name);
        this.members.put(member.getId(),member);
        return member;
    }

    /** Searches catalog using the provided strategy pattern */
    public List<LibraryItem> search(String term, SearchStrategy strategy){
        return strategy.search(term,new ArrayList<>(catalog.values()));
    }

    /** Checks out a book copy to a member */
    public void checkout(String memberId,String copyId){
        Member member = members.get(memberId);
        BookCopy copy = copies.get(copyId);
        if (member != null && copy != null) {
            copy.checkout(member);
        } else {
            System.out.println("Error: Invalid member or copy ID.");
        }
    }

    /** Prints all catalog items with availability info */
    public void printCatalog(){
        System.out.println("\n--- Library Catalog ---");
        for(LibraryItem item : catalog.values()){
            List<BookCopy> copies = item.getCopies();
            System.out.println(item + "Total copies: "+ copies.size() + " Available copies: " + copies.stream().filter(BookCopy::isAvailable).count());
        }
        System.out.println("-----------------------\n");
    }

    /** Returns a checked-out copy to the library */
    public void returnItem(String copyId){
        BookCopy copy = copies.get(copyId);
        if (copy != null) {
            copy.returnItem();
        } else {
            System.out.println("Error: Invalid copy ID.");
        }
    }

    /** Places a hold on an item for a member (only if all copies checked out) */
    public void placeHold(String memberId,String itemId){
        Member member = members.get(memberId);
        LibraryItem item = catalog.get(itemId);
        if (member == null || item == null) {
            System.out.println("Invalid member or item ID.");
            return;
        }

        // NEW: Check if any copy is available — if so, reject the hold
        BookCopy availableCopy = item.getAvailableCopy();
        if (availableCopy != null) {
            System.out.println("A copy of '" + item.getTitle() + "' is available. Please check it out directly.");
            return;
        }

        // All copies are checked out — find one to place the hold on
        BookCopy checkedOutCopy = item.getCopies().stream()
                .filter(c -> !c.isAvailable())
                .findFirst()
                .orElse(null);

        if (checkedOutCopy == null) {
            System.out.println("No copies found for hold.");
            return;
        }

        checkedOutCopy.placeHold(member);
    }
}
