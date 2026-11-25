package librarysystem.decorator;

public abstract class BookDetailsDecorator implements BookDetails {
    protected final BookDetails details;

    protected BookDetailsDecorator(BookDetails details) {
        this.details = details;
    }
}
