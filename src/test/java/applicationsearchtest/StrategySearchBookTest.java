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
		BookRepository.clearBooks();
		bookService = null;
	}

	// ======================= Title Search =======================

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

    // ======================= BookRepository.search() branches =======================

    @Test
    void givenNoStrategy_whenSearch_thenThrowIllegalStateException() {
        // ما بنعيّن أي strategy (أو بنعيّن null) → لازم يرمي IllegalStateException
        bookRepository.setSearchStrategy(null);
        assertThrows(IllegalStateException.class,
                () -> bookRepository.search("AnyKeyword"),
                "Should throw if search strategy is not set");
    }

    // ======================= searchBook(...) static method =======================

    @Test
    void searchBook_NullKeyword_ShouldReturnNull() {
        Book result = BookRepository.searchBook(null);
        assertNull(result, "searchBook(null) should return null");
    }

    @Test
    void searchBook_EmptyKeyword_ShouldReturnNull() {
        Book result = BookRepository.searchBook("");
        assertNull(result, "searchBook(\"\") should return null");
    }

    @Test
    void searchBook_TitleMatch_ShouldReturnBook() {
        Book result = BookRepository.searchBook("Clean Code");
        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void searchBook_AuthorMatch_ShouldReturnBook() {
        Book result = BookRepository.searchBook("Robert Martin");
        assertNotNull(result);
        assertEquals("Robert Martin", result.getAuthor());
    }

    @Test
    void searchBook_IsbnMatch_ShouldReturnBook() {
        Book result = BookRepository.searchBook("222");
        assertNotNull(result);
        assertEquals("222", result.getIsbn());
    }

    @Test
    void searchBook_NoMatch_ShouldReturnNull() {
        Book result = BookRepository.searchBook("NonExistingKeyword");
        assertNull(result, "Should return null when no book matches keyword");
    }

    // ======================= findBookByIsbn(...) =======================

    @Test
    void findBookByIsbn_Found_ShouldReturnBook() {
        Book found = BookRepository.findBookByIsbn("111");
        assertNotNull(found);
        assertEquals("111", found.getIsbn());
    }

    @Test
    void findBookByIsbn_NotFound_ShouldReturnNull() {
        Book found = BookRepository.findBookByIsbn("999");
        assertNull(found);
    }

    // ======================= addBook / removeBook / findAll =======================

    @Test
    void addRemoveAndRetrieveBooks_ShouldReflectInRepository() {
        int initialSize = BookRepository.findAll().size();

        Book extra = new Book("Extra Book", "Extra Author", "999");
        BookRepository.addBook(extra);

        List<Book> afterAdd = BookRepository.findAll();
        assertEquals(initialSize + 1, afterAdd.size());
        assertTrue(afterAdd.contains(extra));

        BookRepository.removeBook(extra);

        List<Book> afterRemove = BookRepository.findAll();
        assertEquals(initialSize, afterRemove.size());
        assertFalse(afterRemove.contains(extra));
    }

    // ======================= getAllBooks / getBooks / setBooks =======================

    @Test
    void getAllBooksAndGetBooks_ShouldReturnInternalListReference() {
        List<Book> allBooks = bookRepository.getAllBooks();
        ArrayList<Book> internalBooks = BookRepository.getBooks();

        assertSame(internalBooks, allBooks, "getAllBooks should return the internal list reference");
        assertEquals(internalBooks.size(), allBooks.size());
    }

    @Test
    void setBooks_ShouldReplaceInternalListReference() {
        ArrayList<Book> newList = new ArrayList<>();
        newList.add(new Book("New Title", "New Author", "999"));

        BookRepository.setBooks(newList);

        assertSame(newList, BookRepository.getBooks(), "setBooks should replace the internal list reference");
        assertEquals(1, BookRepository.getBooks().size());
    }

    // ======================= clearBooks / findAllBorrowed =======================

    @Test
    void clearBooks_ShouldRemoveAllBooks() {
        assertFalse(BookRepository.findAll().isEmpty(), "Precondition: repository should not be empty");
        BookRepository.clearBooks();
        assertTrue(BookRepository.findAll().isEmpty(), "After clearBooks, repository should be empty");
    }

    @Test
    void findAllBorrowed_WhenNoBorrowedBooks_ShouldReturnEmptyList() {
        // بما إنه ما في Book مقترض (isBorrowed=false افتراضياً)، لازم ترجع قائمة فاضية
        List<Book> borrowed = BookRepository.findAllBorrowed();
        assertNotNull(borrowed);
        assertTrue(borrowed.isEmpty(), "No borrowed books should result in empty list");
    }
}
