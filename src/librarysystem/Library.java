
package librarysystem;

import librarysystem.repository.BookRepository;
import librarysystem.observer.LibraryObserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Library implements Iterable<Book> {
    private final BookRepository repo;
    private final List<LibraryObserver> observers = new ArrayList<>();

    public Library(BookRepository repo) {
        this.repo = repo;
    }

    public void addBook(Book book) {
        boolean alreadyStored = repo.findById(book.getBookId()) != null;
        repo.save(book);
        if (!alreadyStored) {
            notifyBookAdded(book);
        }
    }

    public void removeBook(Book book) {
        boolean stored = repo.findById(book.getBookId()) != null;
        repo.remove(book);
        if (stored) {
            notifyBookRemoved(book);
        }
    }

    public Book findBookById(int id) {
        return repo.findById(id);
    }

    public void addObserver(LibraryObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LibraryObserver observer) {
        observers.remove(observer);
    }

    private void notifyBookAdded(Book book) {
        for (LibraryObserver observer : observers) {
            observer.onBookAdded(book);
        }
    }

    private void notifyBookRemoved(Book book) {
        for (LibraryObserver observer : observers) {
            observer.onBookRemoved(book);
        }
    }

    @Override
    public Iterator<Book> iterator() {
        return repo.iterator();
    }
}
