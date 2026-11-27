package librarysystem.command;

import librarysystem.Book;
import librarysystem.facade.LibraryFacade;

import java.util.Scanner;

public class RemoveBookCommand implements MenuCommand {
    private final Scanner scanner;
    private final LibraryFacade libraryFacade;

    public RemoveBookCommand(Scanner scanner, LibraryFacade libraryFacade) {
        this.scanner = scanner;
        this.libraryFacade = libraryFacade;
    }

    @Override
    public boolean execute() {
        int id;
        try {
            System.out.print("Enter the book ID to remove: ");
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
        libraryFacade.removeBookById(id);
        System.out.println(book + " was removed from the library.");
        return true;
    }
}
