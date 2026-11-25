package librarysystem.decorator;

import librarysystem.Book;

public class BasicBookDetails implements BookDetails {
    private final Book book;

    public BasicBookDetails(Book book) {
        this.book = book;
    }

    @Override
    public String describe() {
        return book + "\nID: " + book.getBookId();
    }
}
