package application;

import domain.Book;
import java.util.List;
import persistence.BookRepository;

/**
 * Service layer for managing books in the library system.
 *
 * <p>Acts as an intermediary between the presentation layer and the BookRepository.
 * Provides operations to add new books and search for existing books by keyword.
 * This class keeps the business logic decoupled from the persistence mechanism.
 */
public class BookService {

  private BookRepository repository;

  /**
   * Default constructor.
   * Repository must be set via dependency injection or a setter before usage.
   */
  public BookService() {
  }

  /**
   * Constructs a BookService with the specified repository.
   *
   * @param repository the BookRepository instance to use for storage and retrieval
   */
  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  /**
   * Adds a new book to the repository.
   *
   * @param book the Book object to add
   */
  public void addBook(Book book) {
    repository.addBook(book);
  }

  /**
   * Searches for books matching the given keyword in the title, author, or ISBN.
   *
   * @param keyword the keyword to search for
   * @return a list of books matching the keyword; empty list if none found
   */
  public List<Book> searchBooks(String keyword) {
    return repository.searchBooks(keyword);
  }
}
