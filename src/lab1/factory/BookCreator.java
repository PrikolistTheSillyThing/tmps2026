package lab1.factory;

import lab1.Book;

public abstract class BookCreator {

    public abstract Book createBook(
            String name,
            String author,
            int year
    );
}
