package applicationsearchbooks;

import java.util.List;
import java.util.stream.Collectors;

import domain.Book;

/**
 * Search strategy that matches books by author name.
 *
 * <p>Used by the repository to perform author-based filtering.</p>
 */
public class BookAuthorSearchStrategy implements BookSearchStrategy {

    /**
     * Creates a new {@code BookAuthorSearchStrategy}.
     */
    public BookAuthorSearchStrategy() {
        // default constructor
    }

    /**
     * Filters a list of books by author.
     *
     * @param books  the list of books to search
     * @param keyword the author keyword to match
     * @return a list of books whose author contains the keyword
     */
    @Override
    public List<Book> searchBook(List<Book> books, String keyword) {
        if (books == null || keyword == null) return List.of();
        return books.stream()
            .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    }
}