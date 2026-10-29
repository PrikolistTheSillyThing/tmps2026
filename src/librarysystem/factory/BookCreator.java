package librarysystem.factory;

import librarysystem.Book;

public abstract class BookCreator {

    public abstract Book createBook(
            String name,
            String author,
            int year
    );
}
