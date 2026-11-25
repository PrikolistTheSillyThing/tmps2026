# Library Management System
## Structural Design Patterns: Proxy, Decorator and Facade

The project was implemented in Java as a small console-based library application. This lab extends the project from the previous lab (Singleton, Builder and Factory Method) with three structural design patterns. The patterns are implemented manually using standard Java features; no external libraries or frameworks are required.

## Task Requirements

1. Implement examples of structural design patterns in a Java application.
2. Demonstrate how the patterns organize classes and objects into larger structures without tightly coupling them.
3. Explain how the design improves code organization, maintainability and extensibility.

This lab demonstrates:

- **Proxy**: `LoggingBookRepositoryProxy` stands in for the real repository and adds logging around every repository operation.
- **Decorator**: `BookDetails` decorators add extra information (e-book format, reserved status) to a book description at runtime.
- **Facade**: `LibraryFacade` provides one simple entry point for the common library tasks: creating, adding, removing and finding books.

## Project Overview

The application is still a small console-based library. A user can save, remove and find books by their ID, and now can also view decorated book details through a new menu option. The structural patterns are integrated into the existing application, building on the creational patterns from the previous lab.

### Project Structure

```text
librarysystem
├── Book.java
├── Library.java
├── Main.java
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
├── proxy
│   └── LoggingBookRepositoryProxy.java
└── repository
    ├── BookRepository.java
    └── InMemoryBookRepository.java
```

### Class Diagram

<img width="1214" height="842" alt="image" src="https://github.com/user-attachments/assets/50d2e51d-099a-4c3b-abe5-a08cec5444b3" />


## Part A: Proxy Pattern

> Provide a surrogate or placeholder for another object to control access to it.

The Proxy pattern is implemented in `LoggingBookRepositoryProxy`. It implements the same `BookRepository` interface as the real repository and holds a reference to the real object (the *target*). Every call is first logged and then forwarded to the target. This is a *logging proxy*: it adds behavior around the repository without changing the repository itself.

### Proxy implementation

```java
public class LoggingBookRepositoryProxy implements BookRepository {
    private final BookRepository target;

    public LoggingBookRepositoryProxy(BookRepository target) {
        this.target = target;
    }

    @Override
    public void save(Book book) {
        System.out.println("[Repository log] Saving book: " + book.getName());
        target.save(book);
    }

    @Override
    public void remove(Book book) {
        System.out.println("[Repository log] Removing book: " + book.getName());
        target.remove(book);
    }

    @Override
    public Book findById(int id) {
        System.out.println("[Repository log] Looking up book ID: " + id);
        return target.findById(id);
    }
}
```

### How it works

- The proxy and the real repository share the **`BookRepository` interface**, so they are interchangeable for any client.
- The proxy keeps a **reference to the target** and delegates the actual work to it.
- The proxy performs its **extra work** (printing a log line) before the delegation.

### Integration in Main

`Main` wraps the singleton repository from the previous lab with the proxy:

```java
BookRepository repo = new LoggingBookRepositoryProxy(
        InMemoryBookRepository.getInstance());
Library library = new Library(repo);
```

`Library` depends only on the `BookRepository` interface, so it does not know whether it is using the real repository or the proxy. No existing class had to be modified to add logging.

Example output when a book is added and then looked up:

```text
[Repository log] Saving book: Dune
[Repository log] Looking up book ID: 0
```

### Benefits and trade-offs

- **Transparency:** clients use the proxy exactly as they would use the real repository.
- **Separation of concerns:** logging is kept out of `InMemoryBookRepository`, which stays responsible only for storage.
- **Extensibility:** other proxies (caching, access control) could wrap the same interface in the same way.
- **Trade-off:** the proxy logs the *request*, not its result. For example, a save of an already existing book is logged as "Saving" even though the repository then rejects it as a duplicate. Logging the outcome would require the repository methods to return a result.

## Part B: Decorator Pattern

> Attach additional responsibilities to an object dynamically, providing a flexible alternative to subclassing.

The Decorator pattern is used to build a description of a book. A basic description is created first, and optional decorators then add extra lines to it. The participants are:

| Role | Class |
|------|-------|
| Component | `BookDetails` (interface with `describe()`) |
| Concrete component | `BasicBookDetails` |
| Base decorator | `BookDetailsDecorator` (abstract) |
| Concrete decorators | `EBookDetails`, `ReservedBookDetails` |

### Component and concrete component

```java
public interface BookDetails {
    String describe();
}
```

```java
public class BasicBookDetails implements BookDetails {
    private final Book book;

    public BasicBookDetails(Book book) {
        this.book = book;
    }

    @Override
    public String describe() {
        return book + "\nID: " + book.getBookId();
    }
}
```

### Base decorator

```java
public abstract class BookDetailsDecorator implements BookDetails {
    protected final BookDetails details;

    protected BookDetailsDecorator(BookDetails details) {
        this.details = details;
    }
}
```

The base decorator implements the same interface as the object it wraps and stores the wrapped `BookDetails`. This is what allows decorators to be stacked on top of each other.

### Concrete decorators

```java
public class EBookDetails extends BookDetailsDecorator {
    public EBookDetails(BookDetails details) {
        super(details);
    }

    @Override
    public String describe() {
        return details.describe() + "\nFormat: E-book";
    }
}
```

```java
public class ReservedBookDetails extends BookDetailsDecorator {
    public ReservedBookDetails(BookDetails details) {
        super(details);
    }

    @Override
    public String describe() {
        return details.describe() + "\nStatus: Reserved";
    }
}
```

Each decorator calls the wrapped object's `describe()` first and then appends its own line.

### Using the decorators

Menu option 4 builds the description step by step, depending on the user's answers:

```java
BookDetails details = new BasicBookDetails(book);
if (sc.nextLine().equalsIgnoreCase("yes")) {
    details = new EBookDetails(details);
}
if (sc.nextLine().equalsIgnoreCase("yes")) {
    details = new ReservedBookDetails(details);
}
System.out.println("Book details:\n" + details.describe());
```

Example output when both options are answered with "yes":

```text
Book details:
"Dune" (1965), by Frank Herbert
ID: 0
Format: E-book
Status: Reserved
```

Because each decorator is itself a `BookDetails`, any combination (none, one, or both, in any order) can be created at runtime. The order of wrapping determines the order of the output lines.

### Benefits and trade-offs

- **Flexibility:** behavior is combined at runtime instead of being fixed at compile time.
- **No class explosion:** without the pattern, every combination would need its own subclass (`EBookReservedDetails`, and so on). Here two small decorators cover all four combinations.
- **Open/Closed principle:** a new decorator (for example, `DamagedBookDetails`) can be added without modifying `BasicBookDetails` or the existing decorators.
- **Trade-off:** the decorators currently only add text, and the object graph (several small wrapper objects) is harder to inspect when debugging than a single class would be.

## Part C: Facade Pattern

> Provide a unified interface to a set of interfaces in a subsystem, making the subsystem easier to use.

The Facade pattern is implemented in `LibraryFacade`. Creating and storing a book involves two collaborating objects: `BookCreator` (to build a valid `Book`) and `Library` (to store it). The facade combines them behind three simple methods.

### Facade implementation

```java
public class LibraryFacade {
    private final Library library;
    private final BookCreator bookCreator;

    public LibraryFacade(Library library, BookCreator bookCreator) {
        this.library = library;
        this.bookCreator = bookCreator;
    }

    public Book createAndAddBook(String name, String author, int year) {
        Book book = bookCreator.createBook(name, author, year);
        library.addBook(book);
        return book;
    }

    public boolean removeBookById(int id) {
        Book book = library.findBookById(id);
        if (book == null) {
            return false;
        }

        library.removeBook(book);
        return true;
    }

    public Book findBookById(int id) {
        return library.findBookById(id);
    }
}
```

### How it works

- `createAndAddBook()` hides a two-step workflow: create the book through the factory, then store it through the library.
- `removeBookById()` hides the "find the book, then remove it" workflow and reports whether anything was removed.
- `findBookById()` gives the client one place to look books up.

### Integration in Main

`Main` creates the facade once and passes it to every menu action:

```java
BookCreator creator = new StandardBookCreator();
LibraryFacade libraryFacade = new LibraryFacade(library, creator);
```

Compared with the previous lab, `addBook` no longer needs both a `Library` and a `BookCreator`. It now makes a single call:

```java
Book book = libraryFacade.createAndAddBook(name, author, year);
```

Validation errors from the builder (`IllegalStateException`) still pass through the facade and are caught and displayed by `Main`.

### Benefits and trade-offs

- **Simpler client code:** `Main` depends on one class instead of coordinating `Library`, `BookCreator` and `Book.Builder`.
- **Looser coupling:** if the creation or storage steps change, only the facade needs to be updated.
- **Single entry point:** the typical use cases of the application are visible in one class.
- **Trade-off:** the facade is intentionally thin and does not forbid direct access to the subsystem. In this project `Main` still works with `Book` and `BookDetails` objects directly, and the decorator part of the program does not go through the facade.

## Application Flow

1. `Main` obtains the shared repository from `InMemoryBookRepository.getInstance()` and wraps it in `LoggingBookRepositoryProxy`.
2. `Main` passes the proxy to `Library`, which only knows the `BookRepository` interface.
3. `Main` creates a `StandardBookCreator` and builds `LibraryFacade` from the library and the creator.
4. When the user chooses to add a book, `Main` collects the name, author and publication year and calls `libraryFacade.createAndAddBook()`.
5. The facade asks the creator to build the `Book` (using the builder and its validation), then passes it to `Library`.
6. `Library` calls `save()` on the proxy, which prints a log line and forwards the call to `InMemoryBookRepository`.
7. When the user chooses to view decorated details, `Main` finds the book through the facade and wraps a `BasicBookDetails` in the decorators the user selects.

### Menu Options

```text
Library menu
Press 0 to leave the library
Press 1 to save a book
Press 2 to remove a book
Press 3 to find a book
Press 4 to view decorated book details
```

### Input and Object Validation

- Non-numeric menu choices, IDs and publication years are handled with `NumberFormatException`.
- An empty book name or author, or a publication year less than or equal to zero, is rejected by the builder.
- Searching, removing or decorating a book with an unknown ID prints "No book found" and does nothing else.
- Adding a book that is already present, or removing one that is not, is reported by the repository.

## Overall Logic

- **Proxy:** `LoggingBookRepositoryProxy` controls access to the repository and adds logging without changing it.
- **Decorator:** `BasicBookDetails` plus `EBookDetails` and `ReservedBookDetails` let the description of a book be extended at runtime.
- **Facade:** `LibraryFacade` offers a simple interface for creating, adding, removing and finding books.
- **Combined design:** the facade uses the factory (and through it the builder) and the library; the library stores books through the proxy, which wraps the singleton repository.

The three patterns address different concerns. Proxy wraps an object that has the *same* interface in order to control or extend how it is accessed. Decorator also wraps objects of the same interface, but its purpose is to add *responsibilities* that can be combined freely. Facade does the opposite of wrapping one object: it gives a *simpler* interface over several collaborating ones. Together with the patterns from the previous lab, they keep object creation, storage, presentation and the client code in separate places, which makes the application easier to extend. The cost is a larger number of small classes, and some patterns (like the facade and the single logging proxy) are currently thin because the application is small.
