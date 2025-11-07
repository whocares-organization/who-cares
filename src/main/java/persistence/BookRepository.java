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
 * This implementation uses a static ArrayList to store all book instances during runtime.
 */
public class BookRepository {

  private static ArrayList<Book> books = new ArrayList<>();
  private BookSearchStrategy searchStrategy;
  
  private static final Logger logger = Logger.getLogger(BookRepository.class.getName());
  
  /**
   * Sets the search strategy for the repository.
   *
   * <p>This allows changing the behavior of the search method dynamically.
   * For example, you can use {@link applicationsearchbooks.TitleSearchStrategy},
   * {@link applicationsearchbooks.AuthorSearchStrategy}, or
   * {@link applicationsearchbooks.IsbnSearchStrategy}.</p>
   *
   * @param searchStrategy the search strategy to use for book searches
   */
  public void setSearchStrategy(BookSearchStrategy searchStrategy) {
      this.searchStrategy = searchStrategy;
  }

  /**
   * Searches the repository for books matching the given keyword using the currently set search strategy.
   *
   * <p>Logs the keyword being searched and the number of results found using the {@link java.util.logging.Logger}.</p>
   *
   * <p><b>Important:</b> A search strategy must be set before calling this method, otherwise
   * an {@link IllegalStateException} is thrown.</p>
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

  public static ArrayList<Book> getBooks() {
	return books;
}

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
   * Searches for books by a keyword.
   *
   * <p>The search matches the keyword against the title, author, or ISBN of each book.
   *
   * @param keyword the search keyword
   * @return a list of books matching the keyword
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
  
  public static Book findBookByIsbn(String isbn) {
      return books.stream()
          .filter(b -> isbn.equals(b.getIsbn()))
          .findFirst()
          .map(b -> {
              
              return b;
          })
          .orElseGet(() -> {
              
              return null;
          });
  }
  
  public static void clearBooks() {
	    books.clear();
	}
  
  /**
   * Retrieves a list of all books stored in the repository.
   *
   * <p>This method returns a new {@link ArrayList} containing all the books
   * from the internal collection, preventing external modification of the
   * original list.</p>
   *
   * @return a list of all books in the repository
   */

  public static List<Book> findAll() {
      return new ArrayList<>(books);
  }
  
  public static List<Book> findAllBorrowed() {
      return books.stream().filter(Book::isBorrowed).collect(java.util.stream.Collectors.toList());
  }
  
  
  
}