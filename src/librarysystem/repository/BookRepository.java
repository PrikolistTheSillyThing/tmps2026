package librarysystem.repository;

import librarysystem.Book;

public interface BookRepository {
    void save(Book book);
    void remove(Book book);
    Book findById(int id);
}
