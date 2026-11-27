package librarysystem.command;

import librarysystem.Book;
import librarysystem.facade.LibraryFacade;

import java.util.Scanner;

public class AddBookCommand implements MenuCommand {
    private final Scanner scanner;
    private final LibraryFacade libraryFacade;

    public AddBookCommand(Scanner scanner, LibraryFacade libraryFacade) {
        this.scanner = scanner;
        this.libraryFacade = libraryFacade;
    }

    @Override
    public boolean execute() {
        System.out.print("Book name: ");
        String name = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();

        int year;
        try {
            System.out.print("Publication year: ");
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year. Book was not added.");
            return true;
        }

        try {
            Book book = libraryFacade.createAndAddBook(name, author, year);
            System.out.println(book + " was added to the library.");
            System.out.println("Book ID: " + book.getBookId());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
