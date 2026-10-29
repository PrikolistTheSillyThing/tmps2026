# Library Management System
## SOLID Principles: SRP, OCP and DIP

The project was implemented in Java as a small console-based library application. No external libraries or frameworks were used; the application logic was implemented with standard Java features.

## Task Requirements

1. Implement a small application that follows SOLID principles.
2. Demonstrate at least three of the five principles in the code.
3. Show how the design makes the system easier to extend and maintain.

This lab demonstrates:

- **SRP**: Single Responsibility Principle
- **OCP**: Open/Closed Principle
- **DIP**: Dependency Inversion Principle

## Project Overview

The application allows a user to add, remove and find books by ID through a console menu. The design separates book data, library operations, storage and user interaction.

### Project Structure

```text
lab1
├── Book.java
├── Library.java
├── Main.java
└── repository
    ├── BookRepository.java
    └── InMemoryBookRepository.java
```

### Class Diagram

<img width="1360" height="1130" alt="library_system_uml" src="https://github.com/user-attachments/assets/f7ea4b86-0c53-4893-866f-480d24a9ad01" />

## Part A: Single Responsibility Principle (SRP)

> A class should have only one reason to change.

Each class has a distinct responsibility:

| Class | Responsibility |
|---|---|
| `Book` | Holds the data for one book: ID, name, author and publication year. |
| `Library` | Exposes library operations such as adding, removing and finding books. |
| `BookRepository` | Defines the contract for saving, removing and finding books. |
| `InMemoryBookRepository` | Implements the repository contract using an in-memory `List<Book>`. |
| `Main` | Handles the console menu, user input, input validation and output. |

For example, changing the menu text or input prompts should only require changes in `Main`. Changing how books are stored should be handled by a repository implementation. The `Book` class does not need to know about either the console menu or the storage mechanism.

### Book

`Book` represents a book and holds its data. Its fields are private, and the getters provide access to that data. It does not manage the collection of books or handle console input.

```java
public class Book {
    private static int id = 0;
    private final int bookId;
    private final String name;
    private final String author;
    private final int publicationYear;

    public Book(String name, String author, int year) {
        bookId = id++;
        this.name = name;
        this.author = author;
        publicationYear = year;
    }

    // Getters and toString() omitted.
}
```

### Main

`Main` owns the console interaction. It reads the user's choice and delegates the requested operation to `Library`.

```java
switch (choice) {
    case 0 -> exit = true;
    case 1 -> addBook(lib1);
    case 2 -> removeBook(lib1);
    case 3 -> findBook(lib1);
    default -> System.out.println("Invalid menu option.");
}
```

This keeps console-specific concerns out of `Library` and the repository classes.

## Part B: Open/Closed Principle (OCP)

> Software entities should be open for extension, but closed for modification.

The application defines a `BookRepository` interface that describes the operations required by the library:

```java
package lab1.repository;

import lab1.Book;

public interface BookRepository {
    void save(Book book);
    void remove(Book book);
    Book findById(int id);
}
```

`InMemoryBookRepository` is one implementation of that contract. It uses a list to store books and searches the list by ID.

```java
public class InMemoryBookRepository implements BookRepository {
    private final List<Book> books = new ArrayList<>();

    @Override
    public void save(Book book) {
        if (findById(book.getBookId()) == null) {
            books.add(book);
        } else {
            System.out.println(
                "\"" + book.getName() + "\" already present in the library."
            );
        }
    }

    @Override
    public void remove(Book book) {
        // Checks whether the book exists, then removes it.
    }

    @Override
    public Book findById(int id) {
        return books.stream()
            .filter(book -> book.getBookId() == id)
            .findFirst()
            .orElse(null);
    }
}
```

The design is **open for extension** because another storage implementation can be added, for example `FileBookRepository` or `DatabaseBookRepository`, by implementing `BookRepository`.

It is **closed for modification** in the sense that `Library` can continue using the same interface without needing to know which storage implementation is used. The new repository provides the existing operations through the same contract.

The current application chooses its implementation in `Main`. Selecting a different implementation would require changing the composition setup there, but not the business logic inside `Library`.

## Part C: Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. Both should depend on abstractions.

`Library` is the high-level module because it exposes the application's library operations. `InMemoryBookRepository` is a lower-level detail because it determines how the books are stored. Instead of directly creating or depending on `InMemoryBookRepository`, `Library` depends on the `BookRepository` interface.

```java
public class Library {
    private final BookRepository repo;

    public Library(BookRepository repo) {
        this.repo = repo;
    }

    public void addBook(Book book) {
        repo.save(book);
    }

    public void removeBook(Book book) {
        repo.remove(book);
    }

    public Book findBookById(int id) {
        return repo.findById(id);
    }
}
```

The constructor receives a repository object. This is **constructor dependency injection**: the dependency is supplied from outside the class instead of being created inside `Library`.

The implementation is selected in `Main`:

```java
BookRepository repo = new InMemoryBookRepository();
Library lib1 = new Library(repo);
```

If a new implementation were added, the setup could instead use:

```java
BookRepository repo = new FileBookRepository(); // Hypothetical implementation
Library lib1 = new Library(repo);
```

`FileBookRepository` is only an example; it is not implemented in this project. The important point is that `Library` does not need to change as long as the new class correctly implements `BookRepository`.

This separation also helps testing. A test can pass a fake or test repository into `Library` to verify that library operations delegate correctly, without relying on the real storage implementation.

## Application Flow

1. `Main` creates an `InMemoryBookRepository` and passes it to `Library`.
2. The menu loop reads the user's choice.
3. `Main` collects and validates any required input.
4. `Main` calls the corresponding method on `Library`.
5. `Library` delegates the storage operation to `BookRepository`.
6. The result is returned to `Main` and displayed to the user.

### Menu Options

```text
Library menu
Press 0 to leave the library
Press 1 to save a book
Press 2 to remove a book
Press 3 to find a book
```

### Input Validation

- Non-numeric menu choices and IDs are handled with `NumberFormatException`.
- A non-numeric publication year cancels the add operation.
- Attempts to find or remove an unknown book ID display a message.

## Overall Logic

- **SRP:** Book data, library operations, storage and console interaction are separated into different classes.
- **OCP:** Additional repository implementations can be introduced through the `BookRepository` interface.
- **DIP:** `Library` depends on the repository abstraction rather than a concrete storage class.
- **Maintainability and testability:** Storage details can be replaced, and `Library` can be tested with an alternative repository implementation.

Together, these principles provide a small but practical example of separating responsibilities and making the application easier to extend.
