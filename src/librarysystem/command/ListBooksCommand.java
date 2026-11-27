package librarysystem.command;

import librarysystem.Book;
import librarysystem.facade.LibraryFacade;

public class ListBooksCommand implements MenuCommand {
    private final LibraryFacade libraryFacade;

    public ListBooksCommand(LibraryFacade libraryFacade) {
        this.libraryFacade = libraryFacade;
    }

    @Override
    public boolean execute() {
        boolean hasBooks = false;
        System.out.println("Books in the library:");
        for (Book book : libraryFacade.getBooks()) {
            System.out.println("ID " + book.getBookId() + ": " + book);
            hasBooks = true;
        }

        if (!hasBooks) {
            System.out.println("No books found.");
        }
        return true;
    }
}
