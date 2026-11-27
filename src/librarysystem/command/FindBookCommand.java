package librarysystem.command;

import librarysystem.Book;
import librarysystem.facade.LibraryFacade;

import java.util.Scanner;

public class FindBookCommand implements MenuCommand {
    private final Scanner scanner;
    private final LibraryFacade libraryFacade;

    public FindBookCommand(Scanner scanner, LibraryFacade libraryFacade) {
        this.scanner = scanner;
        this.libraryFacade = libraryFacade;
    }

    @Override
    public boolean execute() {
        int id;
        try {
            System.out.print("Enter the book ID to find: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return true;
        }

        Book book = libraryFacade.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
        } else {
            System.out.println("Book found: " + book);
            System.out.println("ID: " + book.getBookId());
        }
        return true;
    }
}
