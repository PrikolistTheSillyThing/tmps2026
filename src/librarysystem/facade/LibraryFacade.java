package librarysystem.facade;

import librarysystem.Book;
import librarysystem.Library;
import librarysystem.factory.BookCreator;

/**
 * Provides one simple entry point for the application's common library tasks.
 */
public class LibraryFacade {
    private final Library library;
    private final BookCreator bookCreator;

    public LibraryFacade(Library library, BookCreator bookCreator) {
        this.library = library;
        this.bookCreator = bookCreator;
    }

    public Book createAndAddBook(String name, String author, int year) {
        Book book = bookCreator.createBook(name, author, year);
        library.addBook(book);
        return book;
    }

    public boolean removeBookById(int id) {
        Book book = library.findBookById(id);
        if (book == null) {
            return false;
        }

        library.removeBook(book);
        return true;
    }

    public Book findBookById(int id) {
        return library.findBookById(id);
    }
}
