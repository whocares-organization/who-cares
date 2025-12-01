package applicationsearchbooks;

import domain.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that matches books by title.
 */
public class BookTitleSearchStrategy implements BookSearchStrategy {

    /**
     * Creates a new {@code BookTitleSearchStrategy}.
     */
    public BookTitleSearchStrategy() { }

    /**
     * Filters a list of books by title.
     *
     * @param books   the list of books to search
     * @param keyword the title keyword to match
     * @return a list of books whose title contains the keyword
     */
    @Override
    public List<Book> searchBook(List<Book> books, String keyword) {
        if (books == null || keyword == null) return List.of();
        return books.stream()
            .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    }
}