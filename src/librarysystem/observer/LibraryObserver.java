package librarysystem.observer;

import librarysystem.Book;

public interface LibraryObserver {
    void onBookAdded(Book book);

    void onBookRemoved(Book book);
}
