package librarysystem.command;

import librarysystem.Book;
import librarysystem.decorator.BasicBookDetails;
import librarysystem.decorator.BookDetails;
import librarysystem.decorator.EBookDetails;
import librarysystem.decorator.ReservedBookDetails;
import librarysystem.facade.LibraryFacade;

import java.util.Scanner;

public class ShowBookDetailsCommand implements MenuCommand {
    private final Scanner scanner;
    private final LibraryFacade libraryFacade;

    public ShowBookDetailsCommand(Scanner scanner, LibraryFacade libraryFacade) {
        this.scanner = scanner;
        this.libraryFacade = libraryFacade;
    }

    @Override
    public boolean execute() {
        int id;
        try {
            System.out.print("Enter the book ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return true;
        }

        Book book = libraryFacade.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
            return true;
        }

        BookDetails details = new BasicBookDetails(book);
        System.out.print("Show as an e-book? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            details = new EBookDetails(details);
        }
        System.out.print("Show as reserved? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            details = new ReservedBookDetails(details);
        }
        System.out.println("Book details:\n" + details.describe());
        return true;
    }
}
