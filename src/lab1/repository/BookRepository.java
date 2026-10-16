package lab1.repository;

import lab1.Book;

public interface BookRepository {
    void save(Book book);
    void remove(Book book);
    Book findById(int id);
}
