package applicationsearchbooks;

import domain.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that matches books by ISBN.
 *
 * <p>Used by the repository to perform ISBN-based filtering.</p>
 */
public class BookIsbnSearchStrategy implements BookSearchStrategy {

    /**
     * Creates a new {@code BookIsbnSearchStrategy}.
     */
    public BookIsbnSearchStrategy() { }

    /**
     * Returns books whose ISBN exactly equals the provided keyword (case-insensitive).
     * @param books list of books to search (may be empty)
     * @param keyword target ISBN value (must not be null/blank)
     * @return list of matching books (possibly empty)
     */
    @Override
    public List<Book> searchBook(List<Book> books, String keyword) {
        if (books == null || keyword == null) return List.of();
        return books.stream()
                .filter(b -> b.getIsbn() != null && b.getIsbn().equalsIgnoreCase(keyword))
                .collect(Collectors.toList());
    }
}