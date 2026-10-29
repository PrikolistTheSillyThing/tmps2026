package librarysystem;

public class Book {
    private static int id = 0;
    private final int bookId;
    private final String name;
    private final String author;
    private final int publicationYear;

    public Book(Builder builder) {
        bookId = id++;
        this.name = builder.name;
        this.author = builder.author;
        publicationYear = builder.publicationYear;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private String author;
        private int publicationYear;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder publicationYear(int year) {
            this.publicationYear = year;
            return this;
        }

        public Book build() {
            if (name == null || name.isBlank()) {
                throw new IllegalStateException(
                        "Book name is required.");
            }

            if (author == null || author.isBlank()) {
                throw new IllegalStateException(
                        "Book author is required.");
            }

            if (publicationYear <= 0) {
                throw new IllegalStateException(
                        "Publication year must be positive.");
            }

            return new Book(this);
        }
    }

}
