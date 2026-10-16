# Library Management System
## SOLID Principles: SRP, OCP and DIP

The project was created in Java and follows the requirements of the task.  
No external libraries or frameworks were used, all logic was implemented manually.


## Task Requirements

1. Implement a small application that follows the SOLID principles  
2. Demonstrate at least three of the five principles in the code  
3. Show how the design makes the system easier to extend and maintain

This lab demonstrates:

- **SRP**: Single Responsibility Principle
- **OCP**: Open/Closed Principle
- **DIP**: Dependency Inversion Principle


## Project Overview

The application is a small console-based library. A user can save, remove and find books by their ID through a text menu.

### Project Structure

```
lab1
├── Book.java
├── Library.java
├── Main.java
└── repository
    ├── BookRepository.java
    └── InMemoryBookRepository.java
```

<img width="2720" height="2720" alt="library_system_uml_class_diagram" src="https://github.com/user-attachments/assets/a4b8eb0a-2485-497e-b568-34857713c593" />



## Part A: Single Responsibility Principle (SRP)

> A class should have only one reason to change.

Every class in the project does exactly one job:

| Class | Responsibility |
|---|---|
| `Book` | Holds the data of a single book (name, author, year, ID) |
| `Library` | Exposes the library operations (add, remove, find) |
| `BookRepository` | Defines *how* books are stored and retrieved (the contract) |
| `InMemoryBookRepository` | Implements that contract using a `List<Book>` |
| `Main` | Handles user input and console output |

For example, if the menu text needs to change, only `Main` is touched. If the way books are stored changes, only the repository is touched. `Library` and `Book` stay as they are.

### Book

`Book` is a plain data class. It does not know how it is stored or displayed to the user, it only describes a book.

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

    // getters + toString()
}
```

### Main

All the input parsing and printing lives in `Main`, so the business classes stay free of any UI logic:

```java
case 0 -> exit = true;
case 1 -> addBook(lib1);
case 2 -> removeBook(lib1);
case 3 -> findBook(lib1);
default -> System.out.println("Invalid menu option.");
```


## Part B: Open/Closed Principle (OCP)

> Software entities should be open for extension, but closed for modification.

The storage mechanism is hidden behind the `BookRepository` interface:

```java
package lab1.repository;

import lab1.Book;

public interface BookRepository {
    void save(Book book);
    void remove(Book book);
    Book findById(int id);
}
```

`InMemoryBookRepository` is one implementation of it:

```java
public class InMemoryBookRepository implements BookRepository {

    private final List<Book> books = new ArrayList<>();

    @Override
    public void save(Book book) {
        if (findById(book.getBookId()) == null) {
            books.add(book);
        } else
            System.out.println("\"" + book.getName() + "\" already present in the library.");
    }

    @Override
    public void remove(Book book) { /* ... */ }

    @Override
    public Book findById(int id) {
        return books.stream()
                .filter(p -> p.getBookId() == id)
                .findFirst()
                .orElse(null);
    }
}
```

The system is **open for extension**: a new storage type (`FileBookRepository`, `DatabaseBookRepository`, etc.) can be added by writing a new class that implements `BookRepository`.

The system is **closed for modification**: `Library` does not need a single changed line when a new repository is added.


## Part C: Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. Both should depend on abstractions.

`Library` is the high-level module (the business logic). `InMemoryBookRepository` is the low-level module (the storage detail). `Library` never mentions the concrete class, it only knows the `BookRepository` abstraction:

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

The concrete implementation is chosen in one place only, in `Main`, and passed in through the constructor (**constructor injection**):

```java
BookRepository repo = new InMemoryBookRepository();
Library lib1 = new Library(repo);
```

Swapping the storage means changing a single line in `Main`:

```java
BookRepository repo = new FileBookRepository();   // hypothetical new implementation
Library lib1 = new Library(repo);
```

Dependency injection also makes testing easier, since a fake repository can be passed to `Library` without touching the real storage.


## Application Flow

1. `Main` creates the repository and injects it into `Library`
2. The menu loop reads the user's choice
3. Based on the choice, `Main` collects the needed input and calls `Library`
4. `Library` forwards the work to the `BookRepository`
5. The result is printed back to the console by `Main`

### Menu Options

```
Library menu
Press 0 to leave the library
Press 1 to save a book
Press 2 to remove a book
Press 3 to find a book
```

### Input Validation

- Non-numeric menu choices, IDs and years are caught with `NumberFormatException`
- Removing or finding a book with an unknown ID prints a "No book found" message
- Adding a book with an invalid year cancels the operation



## Overall Logic

- Each class has a single, clearly defined responsibility (**SRP**)
- New storage types can be added without changing existing code (**OCP**)
- `Library` depends on the `BookRepository` abstraction instead of a concrete class (**DIP**)
- The project demonstrates how these three principles work together to produce code that is easy to extend, replace and test
