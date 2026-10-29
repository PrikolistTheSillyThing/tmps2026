package librarysystem.factory;

import librarysystem.Book;

public class StandardBookCreator extends BookCreator {

    @Override
    public Book createBook(String name, String author, int year) {
        return Book.builder()
                .name(name)
                .author(author)
                .publicationYear(year)
                .build();
    }
}