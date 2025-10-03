package presentation;

import application.*;
import domain.*;
import persistence.*;
import java.util.*;

/**
 * Library Management System - Main Application
 *
 * Entry point for the library system. This class demonstrates initialization
 * of repositories, services, and sample data for testing purposes.
 *
 * Notes:
 * - The method used to populate books (`BookInitializer.loadInitialBooks`) is temporary
 *   and will be removed in future iterations once dynamic book management is implemented.
 * - For security reasons, administrator usernames and passwords are loaded from an external
 *   text file (`admins.txt`) rather than being hard-coded. This prevents sensitive data 
 *   from being exposed in the source code.
 *
 * Layers:
 * - Domain: Core entities (Admin, Book, AdminStatus)
 * - Application: Business logic and service orchestration
 * - Persistence: Repositories for storing entities
 * - Presentation: Interfaces for user interaction
 *
 * Core features demonstrated:
 * - Loading admins from an external file
 * - Book setup for testing purposes
 * - Searching books
 * - Admin login example
 */
public class MainApp {
	
	private static AdminService adminservice = new AdminService();
	
	private static final BookRepository bookRepo = new BookRepository();
	private static BookService bookservice = new BookService(bookRepo);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AdminFileLoader fileLoader = new AdminFileLoader("admins.txt");
		adminservice.loadAdmins(fileLoader);
		setBook(bookservice);
		

// =================== Just some testing ===================		
		
//		Scanner sc = new Scanner(System.in);
//	    System.out.print("Enter keyword to search: ");
//	    String keyword = sc.nextLine();
//
//	    List<Book> results = bookservice.searchBooks(keyword);
//
//	    if(results.isEmpty()) {
//	        System.out.println("No books found.");
//	    } else {
//	        for(Book b : results) {
//	            System.out.println("Title: " + b.getTitle() +
//	                               ", Author: " + b.getAuthor() +
//	                               ", ISBN: " + b.getIsbn());
//	        }
//	    }
		
		
//		Scanner input = new Scanner(System.in);
//		String userName = input.nextLine();
//		String password = input.nextLine();
//		boolean c = authenticationservice.login(userName, password);
//		if(c==true) {
//			System.out.print("Yes");
//		}
//		else {
//			System.out.print("No");
//		}
		
	}
	
	private static void setBook(BookService service) {
	    service.addBook(new Book("Java Programming", "John Doe", "12345", false));
	    service.addBook(new Book("Python Basics", "Jane Doe", "67890", false));
	    service.addBook(new Book("Clean Code", "Robert C. Martin", "11111", false));
	    service.addBook(new Book("Design Patterns", "GoF", "22222", true));
	}

}
