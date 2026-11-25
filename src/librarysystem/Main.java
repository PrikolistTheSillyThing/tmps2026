
package librarysystem;

import librarysystem.repository.BookRepository;
import librarysystem.repository.InMemoryBookRepository;
import librarysystem.factory.BookCreator;
import librarysystem.factory.StandardBookCreator;
import librarysystem.decorator.BasicBookDetails;
import librarysystem.decorator.BookDetails;
import librarysystem.decorator.EBookDetails;
import librarysystem.decorator.ReservedBookDetails;
import librarysystem.facade.LibraryFacade;
import librarysystem.proxy.LoggingBookRepositoryProxy;

import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        BookRepository repo = new LoggingBookRepositoryProxy(
                InMemoryBookRepository.getInstance());
        Library library = new Library(repo);

        BookCreator creator = new StandardBookCreator();
        LibraryFacade libraryFacade = new LibraryFacade(library, creator);

        boolean exit = false;
        while (!exit) {
            System.out.println("\nLibrary menu");
            System.out.println("Press 0 to leave the library");
            System.out.println("Press 1 to save a book");
            System.out.println("Press 2 to remove a book");
            System.out.println("Press 3 to find a book");
            System.out.println("Press 4 to view decorated book details");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 0 -> exit = true;
                    case 1 -> addBook(libraryFacade);
                    case 2 -> removeBook(libraryFacade);
                    case 3 -> findBook(libraryFacade);
                    case 4 -> showDecoratedDetails(libraryFacade);
                    default -> System.out.println("Invalid menu option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        System.out.println("Goodbye");
        sc.close();
    }

    private static void addBook(LibraryFacade libraryFacade) {
        System.out.print("Book name: ");
        String name = sc.nextLine();

        System.out.print("Author: ");
        String author = sc.nextLine();

        int year;
        try {
            System.out.print("Publication year: ");
            year = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year. Book was not added.");
            return;
        }
        try {
            Book book = libraryFacade.createAndAddBook(name, author, year);

            System.out.println(book + " was added to the library.");
            System.out.println("Book ID: " + book.getBookId());

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeBook(LibraryFacade libraryFacade) {
        int id;

        try {
            System.out.print("Enter the book ID to remove: ");
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return;
        }

        Book book = libraryFacade.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
            return;
        }
        libraryFacade.removeBookById(id);
        System.out.println(book + " was removed from the library.");
    }

    private static void findBook(LibraryFacade libraryFacade) {
        int id;

        try {
            System.out.print("Enter the book ID to find: ");
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return;
        }
        Book book = libraryFacade.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
        } else {
            System.out.println("Book found: " + book);
            System.out.println("ID: " + book.getBookId());
        }
    }

    private static void showDecoratedDetails(LibraryFacade libraryFacade) {
        int id;
        try {
            System.out.print("Enter the book ID: ");
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return;
        }

        Book book = libraryFacade.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
            return;
        }

        BookDetails details = new BasicBookDetails(book);
        System.out.print("Show as an e-book? (yes/no): ");
        if (sc.nextLine().equalsIgnoreCase("yes")) {
            details = new EBookDetails(details);
        }
        System.out.print("Show as reserved? (yes/no): ");
        if (sc.nextLine().equalsIgnoreCase("yes")) {
            details = new ReservedBookDetails(details);
        }

        System.out.println("Book details:\n" + details.describe());
    }
}
