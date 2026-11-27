# Library Management System
## Behavioral Design Patterns: Command, Iterator and Observer

The project was implemented in Java as a small console-based library application. This lab extends the project from the previous labs (Singleton, Builder and Factory Method; then Proxy, Decorator and Facade) with three behavioral design patterns. The patterns are implemented manually using standard Java features; no external libraries or frameworks are required.

## Task Requirements

1. Implement examples of behavioral design patterns in a Java application.
2. Demonstrate how the patterns organize communication and the distribution of responsibilities between objects.
3. Explain how the design improves code organization, maintainability and extensibility.

This lab demonstrates:

- **Command**: every menu action (`AddBookCommand`, `RemoveBookCommand`, `FindBookCommand`, `ShowBookDetailsCommand`, `ListBooksCommand`, `ExitCommand`) is encapsulated in its own object implementing `MenuCommand`.
- **Iterator**: `BookRepository` and `Library` implement `Iterable<Book>`, so the books can be traversed with a `for-each` loop without exposing how they are stored.
- **Observer**: `Library` notifies registered `LibraryObserver` objects (for example `ConsoleLibraryObserver`) whenever a book is added or removed.

## Project Overview

The application is still a small console-based library. A user can save, remove and find books by their ID, view decorated book details and, with the new menu option, list all books. The behavioral patterns are integrated into the existing application, building on the creational and structural patterns from the previous labs.

### Project Structure

```text
librarysystem
├── Book.java
├── Library.java
├── Main.java
├── command
│   ├── MenuCommand.java
│   ├── AddBookCommand.java
│   ├── RemoveBookCommand.java
│   ├── FindBookCommand.java
│   ├── ShowBookDetailsCommand.java
│   ├── ListBooksCommand.java
│   └── ExitCommand.java
├── decorator
│   ├── BookDetails.java
│   ├── BasicBookDetails.java
│   ├── BookDetailsDecorator.java
│   ├── EBookDetails.java
│   └── ReservedBookDetails.java
├── facade
│   └── LibraryFacade.java
├── factory
│   ├── BookCreator.java
│   └── StandardBookCreator.java
├── observer
│   ├── LibraryObserver.java
│   └── ConsoleLibraryObserver.java
├── proxy
│   └── LoggingBookRepositoryProxy.java
└── repository
    ├── BookRepository.java
    └── InMemoryBookRepository.java
```

### Class Diagram

<!-- Replace with your own exported diagram if you prefer, e.g. <img src="..." /> -->

<img width="1416" height="941" alt="image" src="https://github.com/user-attachments/assets/76794eee-48f6-4747-a328-d2af202e176a" />


## Part A: Command Pattern

> Encapsulate a request as an object, thereby letting you parameterize clients with different requests and decouple the sender of a request from its receiver.

The Command pattern is used for the menu of the application. In the previous version, `Main` contained the code of every menu action. Now each action is a separate command object, and `Main` only chooses which command to run. The participants are:

| Role | Class |
|------|-------|
| Command | `MenuCommand` (interface with `execute()`) |
| Concrete commands | `AddBookCommand`, `RemoveBookCommand`, `FindBookCommand`, `ShowBookDetailsCommand`, `ListBooksCommand`, `ExitCommand` |
| Receiver | `LibraryFacade` |
| Invoker / client | `Main` |

### Command interface

```java
public interface MenuCommand {
    boolean execute();
}
```

The return value tells the invoker whether the application should keep running: `true` means "show the menu again", `false` means "stop".

### Concrete commands

Each command receives everything it needs (the `Scanner` and the `LibraryFacade`) through its constructor and performs one action in `execute()`. As an example, `RemoveBookCommand`:

```java
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
```

The simplest command is `ExitCommand`, which does nothing except tell the invoker to stop:

```java
public class ExitCommand implements MenuCommand {
    @Override
    public boolean execute() {
        return false;
    }
}
```

The other commands follow the same structure:

- `AddBookCommand` reads the name, author and year, then calls `libraryFacade.createAndAddBook()`.
- `FindBookCommand` reads an ID and prints the book found by `libraryFacade.findBookById()`.
- `ShowBookDetailsCommand` builds the decorated description of a book (`BasicBookDetails` wrapped in `EBookDetails` and/or `ReservedBookDetails`).
- `ListBooksCommand` prints every book returned by `libraryFacade.getBooks()` (see Part B).

### Invoker: menu in Main

`Main` registers the commands in a map, using the menu number as the key:

```java
Map<Integer, MenuCommand> commands = Map.of(
        0, new ExitCommand(),
        1, new AddBookCommand(sc, libraryFacade),
        2, new RemoveBookCommand(sc, libraryFacade),
        3, new FindBookCommand(sc, libraryFacade),
        4, new ShowBookDetailsCommand(sc, libraryFacade),
        5, new ListBooksCommand(libraryFacade)
);
```

The main loop no longer contains a `switch` with the code of every option. It only looks up the command and executes it:

```java
int choice = Integer.parseInt(sc.nextLine());

MenuCommand command = commands.get(choice);
if (command == null) {
    System.out.println("Invalid menu option.");
} else {
    running = command.execute();
}
```

### Benefits and trade-offs

- **Decoupling:** `Main` (the sender of the request) does not know what a command does or which objects it uses. It only knows the `MenuCommand` interface.
- **Single responsibility:** every menu action lives in its own small class, so each one can be read, tested and changed on its own.
- **Open/Closed principle:** a new menu option only needs a new `MenuCommand` class and one more entry in the map; the loop in `Main` does not change.
- **Uniform handling:** unknown options (`commands.get(choice) == null`) and the exit condition (the boolean result) are handled in one place for all commands.
- **Trade-off:** the commands also read user input and print output themselves, so they mix the "action" with the console interface. The commands also do not support undo, which is one of the typical advantages of the pattern in larger applications.

## Part B: Iterator Pattern

> Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.

The Iterator pattern is used to traverse the collection of books. Clients can go through all the books with a plain `for-each` loop, without knowing that the books are stored in an `ArrayList` inside the repository. In Java the pattern is built into the language through the `Iterable<T>` and `Iterator<T>` interfaces, so the project uses them directly instead of defining its own iterator interface. The participants are:

| Role | Class |
|------|-------|
| Aggregate | `Iterable<Book>` (extended by `BookRepository`, implemented by `Library`) |
| Concrete aggregates | `InMemoryBookRepository`, `LoggingBookRepositoryProxy`, `Library` |
| Iterator | `Iterator<Book>` |
| Client | `ListBooksCommand` |

### Aggregate interface

```java
public interface BookRepository extends Iterable<Book> {
    void save(Book book);
    void remove(Book book);
    Book findById(int id);
}
```

By extending `Iterable<Book>`, every repository is required to be able to produce an iterator over its books.

### Concrete aggregate

```java
@Override
public Iterator<Book> iterator() {
    return List.copyOf(books).iterator();
}
```

`InMemoryBookRepository` creates the iterator over an immutable snapshot (`List.copyOf`) of its internal list. The internal `ArrayList` is never exposed, and a client cannot remove books through the iterator or fail with a `ConcurrentModificationException` if the repository is changed while the iteration is running.

### Delegation through the layers

`Library` and the logging proxy implement the same method by delegating it downwards:

```java
// Library
@Override
public Iterator<Book> iterator() {
    return repo.iterator();
}
```

```java
// LoggingBookRepositoryProxy
@Override
public Iterator<Book> iterator() {
    System.out.println("[Repository log] Listing books");
    return target.iterator();
}
```

`LibraryFacade` exposes the collection to the client only as an `Iterable<Book>`:

```java
public Iterable<Book> getBooks() {
    return library;
}
```

### Using the iterator

`ListBooksCommand` (menu option 5) traverses the books with a `for-each` loop, which the compiler translates to calls of `iterator()`, `hasNext()` and `next()`:

```java
boolean hasBooks = false;
System.out.println("Books in the library:");
for (Book book : libraryFacade.getBooks()) {
    System.out.println("ID " + book.getBookId() + ": " + book);
    hasBooks = true;
}

if (!hasBooks) {
    System.out.println("No books found.");
}
```

Example output with one book stored:

```text
Books in the library:
[Repository log] Listing books
ID 0: "Dune" (1965), by Frank Herbert
```

### Benefits and trade-offs

- **Encapsulation:** the client does not know whether the books are stored in a list, a map or a database. The storage can change without changing `ListBooksCommand`.
- **Uniform traversal:** the same `for-each` loop works with the facade, the library, the proxy and the real repository.
- **Safe traversal:** because the iterator works on a snapshot, the internal list cannot be modified or corrupted by the client.
- **Works with other patterns:** the proxy can log the traversal (`Listing books`) just by implementing `iterator()`, because the iterator is part of the `BookRepository` interface.
- **Trade-off:** copying the list on every traversal costs memory and time proportional to the number of books. For a small in-memory library this is acceptable, but a large data set would need a lazy iterator. The project also uses Java's built-in iterator instead of a custom one, which means only one traversal order (insertion order) is available.

## Part C: Observer Pattern

> Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.

The Observer pattern is used to react to changes in the library. `Library` (the *subject*) keeps a list of observers and tells them whenever a book is added or removed. The code that adds the book does not need to know who is interested in the event. The participants are:

| Role | Class |
|------|-------|
| Observer | `LibraryObserver` (interface) |
| Concrete observer | `ConsoleLibraryObserver` |
| Subject | `Library` |

### Observer interface and concrete observer

```java
public interface LibraryObserver {
    void onBookAdded(Book book);

    void onBookRemoved(Book book);
}
```

```java
public class ConsoleLibraryObserver implements LibraryObserver {
    @Override
    public void onBookAdded(Book book) {
        System.out.println("[Library notification] Added: " + book);
    }

    @Override
    public void onBookRemoved(Book book) {
        System.out.println("[Library notification] Removed: " + book);
    }
}
```

### Subject

`Library` manages the list of observers and notifies all of them when its state changes:

```java
private final List<LibraryObserver> observers = new ArrayList<>();

public void addObserver(LibraryObserver observer) {
    observers.add(observer);
}

public void removeObserver(LibraryObserver observer) {
    observers.remove(observer);
}

private void notifyBookAdded(Book book) {
    for (LibraryObserver observer : observers) {
        observer.onBookAdded(book);
    }
}

private void notifyBookRemoved(Book book) {
    for (LibraryObserver observer : observers) {
        observer.onBookRemoved(book);
    }
}
```

The notifications are triggered only when the content of the repository really changes:

```java
public void addBook(Book book) {
    boolean alreadyStored = repo.findById(book.getBookId()) != null;
    repo.save(book);
    if (!alreadyStored) {
        notifyBookAdded(book);
    }
}

public void removeBook(Book book) {
    boolean stored = repo.findById(book.getBookId()) != null;
    repo.remove(book);
    if (stored) {
        notifyBookRemoved(book);
    }
}
```

If the user tries to add a book that is already present, or to remove one that does not exist, the repository reports the problem and no observer is notified.

### Integration in Main

`Main` registers the observer once, when the library is created:

```java
Library library = new Library(repo);
library.addObserver(new ConsoleLibraryObserver());
```

Example output when a book is added:

```text
[Repository log] Looking up book ID: 0
[Repository log] Saving book: Dune
[Library notification] Added: "Dune" (1965), by Frank Herbert
"Dune" (1965), by Frank Herbert was added to the library.
Book ID: 0
```

Example output when the same book is removed:

```text
[Repository log] Looking up book ID: 0
[Repository log] Looking up book ID: 0
[Repository log] Looking up book ID: 0
[Repository log] Removing book: Dune
[Library notification] Removed: "Dune" (1965), by Frank Herbert
"Dune" (1965), by Frank Herbert was removed from the library.
```

The `[Library notification]` lines come from the observer. The `[Repository log]` lines come from the proxy from the previous lab, and the repeated lookups appear because the command, the facade and the library each check that the book exists before continuing.

### Benefits and trade-offs

- **Loose coupling:** `Library` only knows the `LibraryObserver` interface. It does not know what the observers do with the notification.
- **Extensibility:** new reactions to library events (writing to a file, updating statistics, sending an email) are added as new observer classes and registered with `addObserver()`, without modifying `Library`, the facade or the commands.
- **Runtime flexibility:** observers can be added and removed while the program runs.
- **Consistent notifications:** the check against the repository makes sure observers are told only about real changes.
- **Trade-off:** notifications are synchronous, so a slow observer would block the operation that triggered it. The observers are also called in registration order, and an exception in one of them would stop the notification of the next ones. The project currently has only one observer, so the benefit of the pattern is mostly in the design that is ready for more.

## Application Flow

1. `Main` obtains the shared repository from `InMemoryBookRepository.getInstance()` and wraps it in `LoggingBookRepositoryProxy`.
2. `Main` passes the proxy to `Library` and registers a `ConsoleLibraryObserver` as an observer of the library.
3. `Main` creates a `StandardBookCreator`, builds `LibraryFacade` and creates the six `MenuCommand` objects, which are stored in a map by menu number.
4. In the main loop `Main` reads the menu choice, finds the matching command in the map and calls `execute()`.
5. When the user adds a book, `AddBookCommand` reads the data and calls `libraryFacade.createAndAddBook()`. The facade creates the book through the factory and passes it to `Library`.
6. `Library` stores the book through the proxy and, if the book was really added, calls `onBookAdded()` on every registered observer.
7. When the user lists the books, `ListBooksCommand` iterates over `libraryFacade.getBooks()`. The call goes through `Library` and the proxy to the repository, which returns an iterator over a snapshot of its books.
8. The loop continues while the executed command returns `true`. `ExitCommand` returns `false`, which ends the program.

### Menu Options

```text
Library menu
Press 0 to leave the library
Press 1 to save a book
Press 2 to remove a book
Press 3 to find a book
Press 4 to view decorated book details
Press 5 to list all books
```

### Input and Object Validation

- Non-numeric menu choices, IDs and publication years are handled with `NumberFormatException`.
- An unknown menu number is rejected by `Main` with "Invalid menu option", because no command is registered for it.
- An empty book name or author, or a publication year less than or equal to zero, is rejected by the builder.
- Searching, removing or decorating a book with an unknown ID prints "No book found" and does nothing else.
- Adding a book that is already present, or removing one that is not, is reported by the repository and does not notify the observers.
- Listing an empty library prints "No books found."

## Overall Logic

- **Command:** each menu action is an object implementing `MenuCommand`; `Main` only looks up the command by number and executes it.
- **Iterator:** `BookRepository` and `Library` are `Iterable<Book>`, so the books can be traversed with a `for-each` loop without exposing the internal list.
- **Observer:** `Library` notifies the registered `LibraryObserver` objects when a book is added or removed.
- **Combined design:** the commands call the facade, the facade uses the factory and the library, the library stores books through the proxy and informs its observers, and the same chain provides the iterator used to list the books.

The three patterns address different kinds of communication between objects. Command separates the object that *requests* an action from the object that *performs* it. Iterator separates the *traversal* of a collection from its internal *structure*. Observer lets one object *broadcast* changes to any number of interested objects without knowing them. Together with the patterns from the previous labs, they keep user interaction, creation, storage, traversal and notification in separate places, which makes the application easier to extend. The cost is a larger number of small classes, and some parts (such as the single observer and the in-memory snapshot iterator) are fairly simple because the application is small.
