package persistence;

import domain.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for managing Book entities in temporary storage.
 * 
 * <p>Provides methods to add, remove, retrieve, and search for books.
 * This implementation uses a static ArrayList to store all book instances during runtime.
 */
public class BookRepository {

  private static ArrayList<Book> books = new ArrayList<>();

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
  
  
  
}
