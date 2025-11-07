package presentation;

import application.AdminFileLoader;
import application.AdminService;
import application.BookService;
import application.EmailService;
import application.LoanService;
import application.OverdueEmailObserver;
import domain.Book;
import persistence.BookRepository;

import java.time.LocalDate;

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
  private static LoanService loanService = new LoanService();
 

  /**
   * Main entry point of the application.
   *
   * @param args command-line arguments (not used)
 * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    AdminFileLoader fileLoader = new AdminFileLoader("admins.txt");
    adminService.loadAdmins(fileLoader);
    setBook(bookService);

    // Register overdue email observer
  

    // Example: trigger a scan (in real app schedule daily)
    loanService.scanAndNotifyOverdues(LocalDate.now());
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