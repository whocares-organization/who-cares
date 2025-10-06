package persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import domain.*;

/**
 * Repository class for managing Book entities in temporary storage.
 * <p>
 * Provides methods to add, remove, retrieve, and search for books.
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
     * <p>
     * The search matches the keyword against the title, author, or ISBN of each book.
     * 
     * @param keyword the search keyword
     * @return a list of books matching the keyword
     */
    public List<Book> searchBooks(String keyword) {
        return books.stream()
            .filter(b -> b.getTitle().contains(keyword) ||
                         b.getAuthor().contains(keyword) ||
                         b.getIsbn().contains(keyword))
            .collect(Collectors.toList());
    }
}