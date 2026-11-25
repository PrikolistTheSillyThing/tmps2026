package librarysystem.decorator;

public class ReservedBookDetails extends BookDetailsDecorator {
    public ReservedBookDetails(BookDetails details) {
        super(details);
    }

    @Override
    public String describe() {
        return details.describe() + "\nStatus: Reserved";
    }
}
