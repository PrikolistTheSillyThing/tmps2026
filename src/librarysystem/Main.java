
package librarysystem;

import librarysystem.repository.BookRepository;
import librarysystem.repository.InMemoryBookRepository;
import librarysystem.factory.BookCreator;
import librarysystem.factory.StandardBookCreator;
import librarysystem.command.AddBookCommand;
import librarysystem.command.ExitCommand;
import librarysystem.command.FindBookCommand;
import librarysystem.command.ListBooksCommand;
import librarysystem.command.MenuCommand;
import librarysystem.command.RemoveBookCommand;
import librarysystem.command.ShowBookDetailsCommand;
import librarysystem.facade.LibraryFacade;
import librarysystem.observer.ConsoleLibraryObserver;
import librarysystem.proxy.LoggingBookRepositoryProxy;

import java.util.Scanner;
import java.util.Map;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        BookRepository repo = new LoggingBookRepositoryProxy(
                InMemoryBookRepository.getInstance());
        Library library = new Library(repo);
        library.addObserver(new ConsoleLibraryObserver());

        BookCreator creator = new StandardBookCreator();
        LibraryFacade libraryFacade = new LibraryFacade(library, creator);

        Map<Integer, MenuCommand> commands = Map.of(
                0, new ExitCommand(),
                1, new AddBookCommand(sc, libraryFacade),
                2, new RemoveBookCommand(sc, libraryFacade),
                3, new FindBookCommand(sc, libraryFacade),
                4, new ShowBookDetailsCommand(sc, libraryFacade),
                5, new ListBooksCommand(libraryFacade)
        );

        boolean running = true;
        while (running) {
            System.out.println("\nLibrary menu");
            System.out.println("Press 0 to leave the library");
            System.out.println("Press 1 to save a book");
            System.out.println("Press 2 to remove a book");
            System.out.println("Press 3 to find a book");
            System.out.println("Press 4 to view decorated book details");
            System.out.println("Press 5 to list all books");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                MenuCommand command = commands.get(choice);
                if (command == null) {
                    System.out.println("Invalid menu option.");
                } else {
                    running = command.execute();
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        System.out.println("Goodbye");
        sc.close();
    }

}
