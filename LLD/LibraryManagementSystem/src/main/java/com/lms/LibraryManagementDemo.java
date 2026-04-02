package com.lms;

import com.lms.enums.ItemType;
import com.lms.model.BookCopy;
import com.lms.model.LibraryItem;
import com.lms.model.Member;
import com.lms.service.LibraryManagementSystem;
import com.lms.strategy.SearchByAuthor;
import com.lms.strategy.SearchByTitle;


import java.util.List;

public class LibraryManagementDemo {
    /** Entry point - demonstrates library operations: search, checkout, return, and holds */
    public static void main(String[] args) {
        LibraryManagementSystem library = LibraryManagementSystem.getInstance();

        // --- Setup: Add items and members using the Facade ---
        System.out.println("=== Setting up the Library ===");

        LibraryItem hobbit = library.addItem(ItemType.BOOK, "B001", "The Hobbit", "J.R.R. Tolkien", 2);
        LibraryItem dune = library.addItem(ItemType.BOOK, "B002", "Dune", "Frank Herbert", 1);
        LibraryItem natGeo = library.addItem(ItemType.MAGAZINE, "M001", "The National Geographic", "NatGeo Society", 3);

        Member alice = library.addMember("MEM01", "Alice");
        Member bob = library.addMember("MEM02", "Bob");
        Member charlie = library.addMember("MEM03", "Charlie");
        library.printCatalog();

        // --- Scenario 1: Searching (Strategy Pattern) ---
        System.out.println("\n=== Scenario 1: Searching for Items ===");
        System.out.println("Searching for title 'Dune':");
        library.search("Dune", new SearchByTitle())
                .forEach(item -> System.out.println("Found: " + item.getTitle()) );
        System.out.println("\nSearching for author 'Tolkien':");
        library.search("Tolkien", new SearchByAuthor())
                .forEach(item -> System.out.println("Found: " + item.getTitle()));


        // --- Scenario 2: Checkout and Return (State Pattern) ---
        System.out.println("\n\n=== Scenario 2: Checkout and Return ===");
        library.checkout(alice.getId(), hobbit.getCopies().get(0).getCopyId()); // Alice checks out The Hobbit copy 1
        library.checkout(bob.getId(), dune.getCopies().get(0).getCopyId()); // Bob checks out Dune copy 1
        library.printCatalog();

        System.out.println("Attempting to checkout an already checked-out book:");
        library.checkout(charlie.getId(), hobbit.getCopies().get(0).getCopyId()); // Charlie fails to check out The Hobbit copy 1

        System.out.println("\nAlice returns The Hobbit:");
        library.returnItem(hobbit.getCopies().get(0).getCopyId());

        library.printCatalog();

        library.returnItem(dune.getCopies().get(0).getCopyId());

        library.printCatalog();

        System.out.println("Searching for items containing title :: /'The/'");
        library.search("the",new SearchByTitle()).stream().forEach(System.out::println);


        // --- Scenario 3: Holds and Notifications (Observer Pattern) ---
        System.out.println("\n\n=== Scenario 3: Placing a Hold ===");
        System.out.println("Placehold even available::");
        library.placeHold(charlie.getId(), "B002"); // Charlie places a hold on Dune

        System.out.println("Dune is checked out by Bob. Charlie places a hold.");
        library.checkout(bob.getId(), dune.getCopies().get(0).getCopyId());
        library.placeHold(charlie.getId(), "B002"); // Charlie places a hold on Dune



        System.out.println("\nBob returns Dune. Charlie should be notified.");
        library.returnItem(dune.getCopies().get(0).getCopyId()); // Bob returns Dune

        System.out.println("\nCharlie checks out the book that was on hold for him.");
        library.checkout(charlie.getId(), dune.getCopies().get(0).getCopyId());



        System.out.println("\nTrying to check out the same on-hold item by another member (Alice):");
        library.placeHold(bob.getId(), "B002");
        library.returnItem(dune.getCopies().get(0).getCopyId());
        library.checkout(alice.getId(), dune.getCopies().get(0).getCopyId()); // Alice fails, it's on-hold for BOB.

        library.printCatalog();

    }
}
