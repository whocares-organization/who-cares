package applicationsearchtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import applicationsearchbooks.BookAuthorSearchStrategy;
import applicationsearchbooks.BookIsbnSearchStrategy;
import applicationsearchbooks.BookTitleSearchStrategy;
import applicationsearchbooks.BookSearchStrategy;
import domain.Book;
import persistence.BookRepository;

class BookSearchStrategiesTest {

    private List<Book> sample;

    @BeforeEach
    void setup() {
        BookRepository.clearBooks();
        BookRepository.addBook(new Book("Effective Java", "Joshua Bloch", "ISBN-111"));
        BookRepository.addBook(new Book("Clean Code", "Robert C. Martin", "ISBN-222"));
        BookRepository.addBook(new Book("Design Patterns", "Erich Gamma", "ISBN-333"));
        sample = BookRepository.findAll();
    }

    @Test
    void titleStrategyFindsByPartialTitle() {
        BookSearchStrategy strategy = new BookTitleSearchStrategy();
        List<Book> result = strategy.searchBook(sample, "clean");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void authorStrategyFindsByPartialAuthor() {
        BookSearchStrategy strategy = new BookAuthorSearchStrategy();
        List<Book> result = strategy.searchBook(sample, "bloch");
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
    }

    @Test
    void isbnStrategyFindsExactMatchCaseInsensitive() {
        BookSearchStrategy strategy = new BookIsbnSearchStrategy();
        List<Book> result = strategy.searchBook(sample, "isbn-222");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void strategyReturnsEmptyListForNullBooks() {
        BookSearchStrategy strategy = new BookTitleSearchStrategy();
        List<Book> result = strategy.searchBook(null, "anything");
        assertTrue(result.isEmpty());
    }

    @Test
    void strategyReturnsEmptyListForNullKeyword() {
        BookSearchStrategy strategy = new BookAuthorSearchStrategy();
        List<Book> result = strategy.searchBook(sample, null);
        assertTrue(result.isEmpty());
    }
}
