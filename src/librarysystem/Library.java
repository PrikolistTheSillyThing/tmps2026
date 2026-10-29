
package librarysystem;

import librarysystem.repository.BookRepository;

public class Library {
    private final BookRepository repo;

    public Library(BookRepository repo) {
        this.repo = repo;
    }

    public void addBook(Book book) {
        repo.save(book);
    }

    public void removeBook(Book book) {
        repo.remove(book);
    }

    public Book findBookById(int id) {
        return repo.findById(id);
    }
}
