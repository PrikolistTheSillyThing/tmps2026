package librarysystem.proxy;

import librarysystem.Book;
import librarysystem.repository.BookRepository;

import java.util.Iterator;

public class LoggingBookRepositoryProxy implements BookRepository {
    private final BookRepository target;

    public LoggingBookRepositoryProxy(BookRepository target) {
        this.target = target;
    }

    @Override
    public void save(Book book) {
        System.out.println("[Repository log] Saving book: " + book.getName());
        target.save(book);
    }

    @Override
    public void remove(Book book) {
        System.out.println("[Repository log] Removing book: " + book.getName());
        target.remove(book);
    }

    @Override
    public Book findById(int id) {
        System.out.println("[Repository log] Looking up book ID: " + id);
        return target.findById(id);
    }

    @Override
    public Iterator<Book> iterator() {
        System.out.println("[Repository log] Listing books");
        return target.iterator();
    }
}
