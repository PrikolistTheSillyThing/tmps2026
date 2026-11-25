package librarysystem.decorator;

public class EBookDetails extends BookDetailsDecorator {
    public EBookDetails(BookDetails details) {
        super(details);
    }

    @Override
    public String describe() {
        return details.describe() + "\nFormat: E-book";
    }
}
