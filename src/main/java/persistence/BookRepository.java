package persistence;

import domain.Book;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import applicationsearchbooks.BookSearchStrategy;

/**
 * Repository class for managing Book entities in temporary storage.
 *
 * <p>Provides methods to add, remove, retrieve, and search for books.
 * This implementation uses a static ArrayList to store all book instances during runtime.</p>
 */
public class BookRepository {

  private static ArrayList<Book> books = new ArrayList<>();
  private BookSearchStrategy searchStrategy;
  
  private static final Logger logger = Logger.getLogger(BookRepository.class.getName());
  
  /**
   * Creates a new {@code BookRepository} with empty in-memory storage.
   */
  public BookRepository() { }

  /**
   * Sets the search strategy for the repository.
   *
   * <p>This allows changing the behavior of the search method dynamically.</p>
   *
   * @param searchStrategy the search strategy to use for book searches
   */
  public void setSearchStrategy(BookSearchStrategy searchStrategy) {
      this.searchStrategy = searchStrategy;
  }

  /**
   * Searches the repository for books matching the given keyword using the currently set search strategy.
   *
   * @param keyword the keyword to search for (e.g., part of title, author, or ISBN)
   * @return a list of books matching the keyword; empty if no books match
   * @throws IllegalStateException if no search strategy has been set
   */
  public List<Book> search(String keyword) {
      if (searchStrategy == null) {
          throw new IllegalStateException("Search strategy not set.");
      }

      List<Book> result = searchStrategy.searchBook(books, keyword);

      logger.info("Searching for keyword: " + keyword);
      logger.info("Found " + result.size() + " book(s).");

      return result;
  }

  /**
   * Adds a new book to the repository.
   *
   * @param book the Book object to add
   */
  public static void addBook(Book book) {
    books.add(book);
  }

  /**
   * Returns the internal list reference of books (mutable).
   * Prefer {@link #findAll()} for an immutable snapshot.
   *
   * @return the current internal books list (mutable reference)
   */
  public static ArrayList<Book> getBooks() {
	return books;
}

  /**
   * Replaces the internal books list with the provided list reference.
   *
   * @param books the new list reference to use as storage (must not be {@code null})
   */
  public static void setBooks(ArrayList<Book> books) {
	BookRepository.books = books;
  }

  /**
   * Removes a book from the repository.
   *
   * @param book the Book object to remove
   */
  public static void removeBook(Book book) {
    books.remove(book);
  }

  /**
   * Returns all books currently in the repository.
   *
   * @return a list of all Book objects
   */
  public List<Book> getAllBooks() {
    return books;
  }

  /**
   * Searches for the first book by a keyword across title, author, or ISBN.
   *
   * @param keyword the search keyword
   * @return the first matching book or {@code null} if none found
   */
  public static Book searchBook(String keyword) {
	    if (keyword == null || keyword.isEmpty()) {
	        return null;
	    }

	    String lowerKeyword = keyword.toLowerCase();

	    return books.stream()
	        .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(lowerKeyword))
	                  || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(lowerKeyword))
	                  || (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(lowerKeyword)))
	        .findFirst()
	        .orElse(null); 
	}
  
  /**
   * Finds a book by exact ISBN match.
   *
   * @param isbn the ISBN value to find
   * @return the matching book or {@code null} if not found
   */
  public static Book findBookByIsbn(String isbn) {
      return books.stream()
          .filter(b -> isbn.equals(b.getIsbn()))
          .findFirst()
          .map(b -> b)
          .orElseGet(() -> null);
  }
  
  /** Clears all books in the repository (useful for tests). */
  public static void clearBooks() {
	    books.clear();
	}
  
  /**
   * Retrieves a list of all books stored in the repository.
   *
   * @return a new list copy containing all books
   */
  public static List<Book> findAll() {
      return new ArrayList<>(books);
  }
  
  /**
   * Returns all books currently marked as borrowed.
   *
   * @return list of borrowed books
   */
  public static List<Book> findAllBorrowed() {
      return books.stream().filter(Book::isBorrowed).collect(java.util.stream.Collectors.toList());
  }
}
