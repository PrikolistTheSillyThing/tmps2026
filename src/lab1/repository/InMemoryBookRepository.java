package lab1.repository;

import lab1.Book;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBookRepository implements BookRepository{

    private static final InMemoryBookRepository INSTANCE =
            new InMemoryBookRepository();

    private final List<Book> books = new ArrayList<>();

    private InMemoryBookRepository() {
    }

    public static InMemoryBookRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public void save(Book book) {
        if (findById(book.getBookId()) == null) {
            books.add(book);
        }
        else
            System.out.println("\"" + book.getName() +  "\" already present in the library.");
    }

    @Override
    public void remove(Book book) {
        if (findById(book.getBookId()) != null) {
            books.remove(book);
        }
        else
            System.out.println("\"" + book.getName() +  "\" does not exist in the library.");
    }

    @Override
    public Book findById(int id) {
        return books.stream()
                .filter(p -> p.getBookId() == id)
                .findFirst()
                .orElse(null);
    }
}
