package application;

import domain.Book;
import domain.Member;

import java.time.LocalDate;
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
  
  //=================worked by montaser===========================//////////
  
  
  /**
   * Allows a library member to borrow a book using its ISBN.
   *
   * <p>This method first checks if the member is eligible to borrow books
   * (i.e., has no outstanding fines). If eligible, it searches the repository
   * for the book by its ISBN. If the book exists and is not already borrowed,
   * the book's status is updated to borrowed, the borrow date is set to the current date,
   * and the due date is set to 28 days from now. Otherwise, an appropriate message
   * is displayed.</p>
   *
   * @param isbn    the ISBN of the book to borrow
   * @param member  the member who is borrowing the book
   */
  /*public void borrowBook(String isbn ,Member member) {
	  
	  if (!member.canBorrow()) {
	        System.out.println("You cannot borrow a new book until you pay your fines!");
	        return;
	    }
	  
	  
	  
	 Book b=repository.findBookByIsbn(isbn);
	 if(b!=null&&!b.isBorrowed())
	 {
		 b.setBorrowed(true);
		 b.setBorrowDate(LocalDate.now());
		  b.setDueDate(LocalDate.now().plusDays(28));
		    System.out.println("Borrowed successfully! Due date: " + b.getDueDate());
	    } else {
	    	System.out.println("Book not available or already borrowed.");
	    }
	  
  }
  
  /**
   * Checks all borrowed books in the repository and updates their overdue status.
   *
   * <p>This method retrieves all books from the {@link BookRepository} and iterates through them.
   * For each book, if it is currently borrowed and its due date has passed compared to the current date,
   * the method marks the book as overdue and displays a message indicating which book is overdue.</p>
   *
   * <p>This method does not return any value; it performs status updates directly
   * on the book objects stored in the repository.</p>
   */
  
  /*public void checkOverdueBooks() {
	    List<Book> allBooks = BookRepository.findAll();
	    for (Book b : allBooks) {
	        if (b.isBorrowed() && b.getDueDate() != null && LocalDate.now().isAfter(b.getDueDate())) {
	            b.setOverdue(true);
	            System.out.println("Book " + b.getTitle() + " is overdue!");
	        }
	    }
	}

  
  */
}


