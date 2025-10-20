package application;

import domain.Book;
import java.util.List;
import java.util.logging.Logger;

import persistence.BookRepository;

/**
 * Service layer for managing books in the library system.
 *
 * <p>Acts as an intermediary between the presentation layer and the BookRepository.
 * Provides operations to add new books and search for existing books by keyword.
 * This class keeps the business logic decoupled from the persistence mechanism.
 */
public class BookService {
	 private static final Logger LOGGER = Logger.getLogger(AdminFileLoader.class.getName());

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
  public Boolean addBook(Book book) {
	  
	  if(book==null) {
		  LOGGER.warning("Cannot add book: title is null or empty");
		  return null;
	  }
	  
	  if(book.getTitle() == null || book.getTitle().isEmpty()) {
		    LOGGER.warning("book title must not be empty or null");
		    return false;
		}
	  
	  if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
		    LOGGER.warning("Book author must not be null or empty");
		    return false;
		} 

		if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
		    LOGGER.warning("Book ISBN must not be null or empty");
		    return false;
		}
		
		 Book existing = searchBooks(book.getIsbn());
	 
		if (existing!=null) {
		        LOGGER.warning("Book with ISBN " + book.getIsbn() + " already exists");
		        return false;
		 }

	  
    repository.addBook(book);
    return true;
  }
  
  public Boolean removeBook(Book book) {
      if (book == null) {
          LOGGER.warning("Cannot remove book: book is null");
          return null;
      }

      if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
          LOGGER.warning("Cannot remove book: ISBN is null or empty");
          return false;
      }

      Book existing = searchBooks(book.getIsbn());

      if (existing == null) {
          LOGGER.warning("Book with ISBN " + book.getIsbn() + " does not exist");
          return false;
      }

      repository.removeBook(existing);
      LOGGER.info("Book with ISBN " + book.getIsbn() + " removed successfully");
      return true;
  }

  public BookRepository getRepository() {
	return repository;
}

  public void setRepository(BookRepository repository) {
	this.repository = repository;
  }

  /**
   * Searches for books matching the given keyword in the title, author, or ISBN.
   *
   * @param keyword the keyword to search for
   * @return a list of books matching the keyword; empty list if none found
   */
  public Book searchBooks(String keyword) {
    return repository.searchBook(keyword);
  }
}
