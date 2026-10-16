package lab1;

public class Book {
    private static int id = 0;
    private final int bookId;
    private final String name;
    private final String author;
    private final int publicationYear;

    public Book(String name, String author, int year) {
        bookId = id++;
        this.name = name;
        this.author = author;
        publicationYear = year;
    }

    public int getBookId() {
        return bookId;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    @Override
    public String toString() {
        return "\"" + name + "\" (" + publicationYear + "), by " + author;
    }


}
