
package lab1;

import lab1.repository.BookRepository;
import lab1.repository.InMemoryBookRepository;
import lab1.factory.BookCreator;
import lab1.factory.StandardBookCreator;

import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        BookRepository repo = InMemoryBookRepository.getInstance();
        Library lib1 = new Library(repo);

        BookCreator creator = new StandardBookCreator();

        boolean exit = false;
        while (!exit) {
            System.out.println("\nLibrary menu");
            System.out.println("Press 0 to leave the library");
            System.out.println("Press 1 to save a book");
            System.out.println("Press 2 to remove a book");
            System.out.println("Press 3 to find a book");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 0 -> exit = true;
                    case 1 -> addBook(lib1, creator);
                    case 2 -> removeBook(lib1);
                    case 3 -> findBook(lib1);
                    default -> System.out.println("Invalid menu option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        System.out.println("Goodbye");
        sc.close();
    }

    private static void addBook(Library lib, BookCreator creator) {
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
            Book book = creator.createBook(name, author, year);

            lib.addBook(book);

            System.out.println(book + " was added to the library.");
            System.out.println("Book ID: " + book.getBookId());

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeBook(Library lib) {
        int id;

        try {
            System.out.print("Enter the book ID to remove: ");
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return;
        }

        Book book = lib.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
            return;
        }
        lib.removeBook(book);
        System.out.println(book + " was removed from the library.");
    }

    private static void findBook(Library lib) {
        int id;

        try {
            System.out.print("Enter the book ID to find: ");
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
            return;
        }
        Book book = lib.findBookById(id);
        if (book == null) {
            System.out.println("No book found with ID " + id + ".");
        } else {
            System.out.println("Book found: " + book);
            System.out.println("ID: " + book.getBookId());
        }
    }
}
