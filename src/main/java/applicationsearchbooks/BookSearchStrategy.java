package applicationsearchbooks;
import java.util.List;

import domain.Book;

/**
 * Strategy interface for searching collections of books by a specific criterion.
 */
public interface BookSearchStrategy {

    /**
     * Searches a list of books using an implementation-specific criterion.
     * @param books list of books to search (may be empty)
     * @param keyword search value (semantics depend on implementation)
     * @return list of matching books (possibly empty)
     */
    List<Book> searchBook(List<Book> books, String keyword);

}