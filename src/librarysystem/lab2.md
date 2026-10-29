# Library Management System
## Design Patterns: Singleton, Builder and Factory Method

The project was implemented in Java as a small console-based library application. The design patterns are implemented manually using standard Java features; no external libraries or frameworks are required.

## Task Requirements

1. Implement examples of design patterns in a Java application.
2. Demonstrate how the patterns solve common object-creation and shared-state problems.
3. Explain how the design improves code organization, maintainability and extensibility.

This lab demonstrates:

- **Singleton**: ensures that the application uses one shared `InMemoryBookRepository` instance.
- **Builder**: constructs `Book` objects step by step and validates their required fields.
- **Factory Method**: delegates book creation to a creator abstraction, allowing the concrete creation logic to be changed or extended.

## Project Overview

The application is a small console-based library. A user can save, remove and find books by their ID through a text menu. The patterns are integrated into the existing application rather than implemented as isolated examples.

### Project Structure

```text
librarysystem
├── Book.java
├── Library.java
├── Main.java
├── factory
│   ├── BookCreator.java
│   └── StandardBookCreator.java
└── repository
    ├── BookRepository.java
    └── InMemoryBookRepository.java
```

### Class Diagram

<img width="1360" height="1130" alt="library_system_uml" src="https://github.com/user-attachments/assets/00576279-23f6-4e67-9e40-723666eb1ddf" />

## Part A: Singleton Pattern

> Ensure a class has only one instance and provide a global access point to it.

The Singleton pattern is implemented in `InMemoryBookRepository`. The repository stores the books in a list. If the application created a separate repository each time it needed storage, each repository would have its own list, and books saved through one instance would not be visible through another.

The implementation uses a private constructor, a `static final` instance, and a public `getInstance()` method:

```java
public class InMemoryBookRepository implements BookRepository {

    private static final InMemoryBookRepository INSTANCE =
            new InMemoryBookRepository();

    private final List<Book> books = new ArrayList<>();

    private InMemoryBookRepository() {
    }

    public static InMemoryBookRepository getInstance() {
        return INSTANCE;
    }

    // Repository operations omitted.
}
```

### How it works

- The **private constructor** prevents other classes from creating an instance with `new InMemoryBookRepository()`.
- The **static final `INSTANCE` field** holds the one repository instance.
- The **`getInstance()` method** gives callers access to that shared instance.
- The instance is initialized when the class is initialized. Java's class-initialization mechanism makes this eager initialization thread-safe.

`Main` obtains the repository through the accessor:

```java
BookRepository repo = InMemoryBookRepository.getInstance();
Library lib1 = new Library(repo);
```

The variable is declared as the `BookRepository` interface, so `Library` still depends on the abstraction rather than on the concrete repository class.

### Benefits and trade-offs

The pattern ensures that the application shares one in-memory collection of books and avoids accidentally creating multiple independent repository instances. However, Singleton also introduces shared global state and can make tests less isolated: data can remain in the repository between operations or tests. For a larger application, explicitly passing a repository instance through dependency injection may be easier to test and configure.

## Part B: Builder Pattern

> Separate the construction of a complex object from its representation, allowing it to be created step by step.

The Builder pattern is implemented as the static nested `Book.Builder` class. A book requires a name, author and publication year. Instead of passing all values directly to a constructor, the caller sets each value through a chain of methods and then calls `build()`.

### Builder implementation

```java
public static class Builder {

    private String name;
    private String author;
    private int publicationYear;

    public Builder name(String name) {
        this.name = name;
        return this;
    }

    public Builder author(String author) {
        this.author = author;
        return this;
    }

    public Builder publicationYear(int year) {
        this.publicationYear = year;
        return this;
    }

    public Book build() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException(
                    "Book name is required.");
        }

        if (author == null || author.isBlank()) {
            throw new IllegalStateException(
                    "Book author is required.");
        }

        if (publicationYear <= 0) {
            throw new IllegalStateException(
                    "Publication year must be positive.");
        }

        return new Book(this);
    }
}
```

The `Book` constructor receives the builder and copies its values into the book's final fields:

```java
public Book(Builder builder) {
    bookId = id++;
    this.name = builder.name;
    this.author = builder.author;
    this.publicationYear = builder.publicationYear;
}

public static Builder builder() {
    return new Builder();
}
```

### Using the Builder

The builder allows the book's fields to be specified in a readable sequence:

```java
Book book = Book.builder()
        .name(name)
        .author(author)
        .publicationYear(year)
        .build();
```

In this project, the Builder is used by `StandardBookCreator` when creating a book. Calling `build()` validates the data before a `Book` object is returned. If a required value is missing or invalid, an `IllegalStateException` is thrown. `Main` catches that exception and displays its message instead of adding an invalid book.

### Benefits

- **Readability:** each field is named at the point where it is set.
- **Validation:** required fields are checked in one place before the object is created.
- **Maintainability:** if construction becomes more complicated, the builder can handle that complexity without adding a long list of constructor parameters.
- **Immutability of book fields:** the resulting `Book` stores its main properties in `final` fields.

The Builder is useful here because the creation process is explicit and validation is centralized. For only three simple fields, a constructor could also be reasonable; the pattern becomes more valuable as the number of optional or interdependent fields grows.

## Part C: Factory Method Pattern

> Define a creation method in a base type and let a subclass decide which concrete creation implementation to use.

The Factory Method pattern is represented by `BookCreator` and `StandardBookCreator`. The abstract creator defines the method contract, while the concrete creator implements how a book is created.

### Abstract creator

```java
public abstract class BookCreator {

    public abstract Book createBook(
            String name,
            String author,
            int year
    );
}
```

`BookCreator` declares the creation operation but does not specify its implementation. Code that uses a creator can work with this abstract type.

### Concrete creator

```java
public class StandardBookCreator extends BookCreator {

    @Override
    public Book createBook(String name, String author, int year) {
        return Book.builder()
                .name(name)
                .author(author)
                .publicationYear(year)
                .build();
    }
}
```

`StandardBookCreator` implements the factory method and uses the Builder pattern to assemble the object. The two patterns therefore have different responsibilities: the factory decides which creation implementation is used, while the builder handles the step-by-step construction and validation of a `Book`.

### Integration in Main

`Main` creates a creator and passes it to the book-adding operation:

```java
BookCreator creator = new StandardBookCreator();

// When the user chooses to add a book:
addBook(lib1, creator);
```

The `addBook` method calls the abstraction rather than directly constructing a `Book`:

```java
Book book = creator.createBook(name, author, year);
lib.addBook(book);
```

### Extensibility

A different creator could later implement a different book-creation process—for example, a creator that applies another set of defaults or creates a specialized book type if the model is extended. The calling code can continue using `BookCreator`.

In the current project, only `StandardBookCreator` exists, and it always creates the same `Book` type. The benefit is therefore primarily a separation of responsibilities and a defined extension point; the application does not yet demonstrate switching between multiple concrete creators at runtime.

## Application Flow

1. `Main` obtains the shared repository from `InMemoryBookRepository.getInstance()`.
2. `Main` passes the repository to `Library`.
3. `Main` creates a `StandardBookCreator`, referenced through the `BookCreator` abstraction.
4. When the user chooses to add a book, `Main` collects the name, author and publication year.
5. `BookCreator.createBook()` delegates creation to `StandardBookCreator`.
6. `StandardBookCreator` uses `Book.Builder` to set the fields and validate them.
7. The resulting `Book` is passed to `Library`, which delegates storage to the repository.
8. The repository stores the book in the shared list.

### Menu Options

```text
Library menu
Press 0 to leave the library
Press 1 to save a book
Press 2 to remove a book
Press 3 to find a book
```

### Input and Object Validation

- Non-numeric menu choices, IDs and publication years are handled with `NumberFormatException`.
- An empty book name or author is rejected by the builder.
- A publication year less than or equal to zero is rejected by the builder.
- Invalid book data results in an error message, and the book is not added.

## Overall Logic

- **Singleton:** `InMemoryBookRepository` provides one shared in-memory repository instance.
- **Builder:** `Book.Builder` assembles a book through a readable chain of calls and validates the required fields before construction.
- **Factory Method:** `BookCreator` defines the creation contract, and `StandardBookCreator` provides its implementation.
- **Combined design:** the factory uses the builder, while the application uses the singleton repository for shared storage.

The patterns address different concerns rather than duplicating one another. Singleton controls access to the repository instance, Builder organizes the construction of a valid `Book`, and Factory Method separates the request to create a book from the concrete creation logic. This makes the code easier to understand and gives it clear places for future changes, while also introducing trade-offs—particularly the shared state associated with Singleton and the extra abstraction of a factory when only one creator is currently available.
