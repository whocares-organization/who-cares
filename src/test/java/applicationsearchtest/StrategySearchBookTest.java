package applicationsearchtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import applicationsearchbooks.BookAuthorSearchStrategy;
import applicationsearchbooks.BookIsbnSearchStrategy;
import applicationsearchbooks.BookSearchStrategy;
import applicationsearchbooks.BookTitleSearchStrategy;
import domain.Book;
import persistence.BookRepository;
import application.BookService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StrategySearchBookTest {
	
	 private BookService bookService;
	 private BookRepository bookRepository;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		 bookService = new BookService();
		 bookRepository = new BookRepository(); 
		 bookService = new BookService(bookRepository);
	     bookService.addBook(new Book("Clean Code", "Robert Martin", "111"));
	     bookService.addBook(new Book("The Pragmatic Programmer", "Andrew Hunt", "222"));
	     bookService.addBook(new Book("Code Complete", "Steve McConnell", "333"));
	}

	@AfterEach
	void tearDown() throws Exception {
		bookRepository.clearBooks();
		bookService = null;
	}

	@Test
	void givenTitleKeyword_whenUsingTitleSearch_thenReturnMatchingBook() {
		
		BookSearchStrategy strategy = new BookTitleSearchStrategy();
		bookRepository.setSearchStrategy(strategy);
		List<Book> result = bookRepository.search("Clean Code");

		assertEquals(1, result.size());
		assertEquals("Clean Code", result.get(0).getTitle());
	}
	
	@Test
    void searchByTitle_NoMatch_ShouldReturnEmptyList() {
        BookSearchStrategy strategy = new BookTitleSearchStrategy();
        bookRepository.setSearchStrategy(strategy);

        List<Book> result = bookRepository.search("NonExistingTitle");

        assertTrue(result.isEmpty(), "Should return empty list if title not found");
    }
	
	// ======================= Author Search =======================
    @Test
    void searchByAuthor_ShouldReturnCorrectBook() {
        BookSearchStrategy strategy = new BookAuthorSearchStrategy();
        bookRepository.setSearchStrategy(strategy);

        List<Book> result = bookRepository.search("Robert Martin");

        assertEquals(1, result.size());
        assertEquals("Robert Martin", result.get(0).getAuthor());
    }
    
    @Test
    void searchByAuthor_NoMatch_ShouldReturnEmptyList() {
        BookSearchStrategy strategy = new BookAuthorSearchStrategy();
        bookRepository.setSearchStrategy(strategy);

        List<Book> result = bookRepository.search("NonExistingAuthor");

        assertTrue(result.isEmpty(), "Should return empty list if author not found");
    }
    
	// ======================= ISBN Search =======================
    @Test
    void searchByIsbn_ShouldReturnCorrectBook() {
        BookSearchStrategy strategy = new BookIsbnSearchStrategy();
        bookRepository.setSearchStrategy(strategy);

        List<Book> result = bookRepository.search("222");

        assertEquals(1, result.size());
        assertEquals("222", result.get(0).getIsbn());
    }

    @Test
    void searchByIsbn_NoMatch_ShouldReturnEmptyList() {
        BookSearchStrategy strategy = new BookIsbnSearchStrategy();
        bookRepository.setSearchStrategy(strategy);

        List<Book> result = bookRepository.search("999");

        assertTrue(result.isEmpty(), "Should return empty list if ISBN not found");
    }

}
