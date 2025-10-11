package presentation;

import application.AdminFileLoader;
import application.AdminService;
import application.BookService;
import domain.Book;
import persistence.BookRepository;

/**
 * Library Management System - Main Application
 *
 * <p>Entry point for the library system. This class demonstrates initialization
 * of repositories, services, and sample data for testing purposes.
 *
 * <p>Notes:
 * - The method used to populate books (`BookInitializer.loadInitialBooks`) is temporary
 *   and will be removed in future iterations once dynamic book management is implemented.
 * - For security reasons, administrator usernames and passwords are loaded from an external
 *   text file (`admins.txt`) rather than being hard-coded. This prevents sensitive data 
 *   from being exposed in the source code.
 *
 * <p>Layers:
 * - Domain: Core entities (Admin, Book, AdminStatus)
 * - Application: Business logic and service orchestration
 * - Persistence: Repositories for storing entities
 * - Presentation: Interfaces for user interaction
 *
 * <p>Core features demonstrated:
 * - Loading admins from an external file
 * - Book setup for testing purposes
 * - Searching books
 * - Admin login example
 */
public class MainApp {

  private static AdminService adminService = new AdminService();

  private static final BookRepository BOOK_REPO = new BookRepository();
  private static BookService bookService = new BookService(BOOK_REPO);

  /**
   * Main entry point of the application.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    AdminFileLoader fileLoader = new AdminFileLoader("admins.txt");
    adminService.loadAdmins(fileLoader);
    setBook(bookService);

    // =================== Just some testing =================== 
    // Uncomment the code below to test searching or login functionality
    // Scanner sc = new Scanner(System.in);
    // System.out.print("Enter keyword to search: ");
    // String keyword = sc.nextLine();
    //
    // List<Book> results = bookservice.searchBooks(keyword);
    //
    // if(results.isEmpty()) {
    //     System.out.println("No books found.");
    // } else {
    //     for(Book b : results) {
    //         System.out.println("Title: " + b.getTitle() +
    //                            ", Author: " + b.getAuthor() +
    //                            ", ISBN: " + b.getIsbn());
    //     }
    // }
  }

  /**
   * Initializes books for testing purposes.
   *
   * @param service the BookService to add books to
   */
  private static void setBook(final BookService service) {
    service.addBook(new Book("Java Programming", "John Doe", "12345", false));
    service.addBook(new Book("Python Basics", "Jane Doe", "67890", false));
    service.addBook(new Book("Clean Code", "Robert C. Martin", "11111", false));
    service.addBook(new Book("Design Patterns", "GoF", "22222", true));
  }
}
