package librarysystem.observer;

import librarysystem.Book;

public class ConsoleLibraryObserver implements LibraryObserver {
    @Override
    public void onBookAdded(Book book) {
        System.out.println("[Library notification] Added: " + book);
    }

    @Override
    public void onBookRemoved(Book book) {
        System.out.println("[Library notification] Removed: " + book);
    }
}
