package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.BookService;
import domain.Book;

class BookServiceTest {
	
	BookService bookService;

	// ================= Setup & Teardown =================
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		bookService = new BookService();
		bookService.addBook(new Book("Olds", "Majd", "12345"));	
	}

	@AfterEach
	void tearDown() throws Exception {
		bookService.getRepository().clearBooks();
		bookService = null;
	}
	// =====================================================

	// ================= Add Book Tests =================
	@Test
	void givenNullBook_whenAddBook_thenDoNotAdd() {
		Boolean notValid = bookService.addBook(null);
        assertNull(notValid, "Book should not be added when input is null");
	}
	
	@Test
	void givenBookWithEmptyTitle_whenAddBook_thenDoNotAdd() {
	    boolean notValid = bookService.addBook(new Book("", "Majd", "123456"));
	    assertFalse(notValid, "Book title should not be empty or null");
	}
	
	@Test
	void givenBookWithEmptyAuthor_whenAddBook_thenDoNotAdd() {
		 boolean notValid = bookService.addBook(new Book("Book", "", "123456"));
	     assertFalse(notValid, "Book author should not be empty or null");
	}
	 
	@Test
	void givenBookWithEmptyIsbn_whenAddBook_thenDoNotAdd() {
		 boolean notValid = bookService.addBook(new Book("Book", "majd", ""));
		 assertFalse(notValid, "Book Isbn should not be empty or null");
	}
	 
	@Test
	void givenDuplicateIsbn_whenAddBook_thenReturnFalse() {
	    Book book = new Book("Olds", "Majd", "12345");
	    Boolean result = bookService.addBook(book);
	    assertFalse(result, "Adding book with existing ISBN should return false");
	}
	 
	@Test
	void givenValidBook_whenAddBook_thenAddToRepository() {
	     Book book = new Book("Title", "Author", "123456");
	     boolean valid = bookService.addBook(book);

	     assertEquals(2, bookService.getRepository().getBooks().size(), "Repository should contain 2 books after adding");
	     assertTrue(valid, "The book should be added successfully");
	}
	// =====================================================

	// ================= Search Book Tests =================
	@Test
	void searchByTitle_ShouldReturnCorrectBook() {
	    Book result = bookService.searchBooks("Olds");
	    assertNotNull(result, "Book with title 'Olds' should be found");
	    assertEquals("Olds", result.getTitle());
	}

	@Test
	void searchByAuthor_ShouldReturnCorrectBook() {
	    Book result = bookService.searchBooks("Majd");
	    assertNotNull(result, "Book authored by 'Majd' should be found");
	    assertEquals("Majd", result.getAuthor());
	}

	@Test
	void searchByIsbn_ShouldReturnCorrectBook() {
	    Book result = bookService.searchBooks("12345");
	    assertNotNull(result, "Book with ISBN '12345' should be found");
	    assertEquals("12345", result.getIsbn());
	}

	@Test
	void searchWithNonExistingKeyword_ShouldReturnNull() {
	    Book result = bookService.searchBooks("NonExisting");
	    assertNull(result, "Searching for a non-existing keyword should return null");
	}

	// ================= Remove Book Tests =================
    @Test
    void removeNullBook_ShouldReturnNull() {
        Boolean result = bookService.removeBook(null);
        assertNull(result, "Removing null book should return null");
    }

    @Test
    void removeBookWithEmptyIsbn_ShouldReturnFalse() {
        Book book = new Book("Some Book", "Author", "");
        Boolean result = bookService.removeBook(book);
        assertFalse(result, "Removing a book with empty ISBN should return false");
    }

    @Test
    void removeBookWithNonExistingIsbn_ShouldReturnFalse() {
        Book book = new Book("NonExisting", "Author", "99999");
        Boolean result = bookService.removeBook(book);
        assertFalse(result, "Removing a book with non-existing ISBN should return false");
    }

    @Test
    void removeExistingBook_ShouldReturnTrueAndRemove() {
        Book book = new Book("Olds", "Majd", "12345");
        Boolean result = bookService.removeBook(book);
        assertTrue(result, "Removing existing book should return true");
        assertNull(bookService.searchBooks("12345"), "Book should no longer exist in repository");
    }
    // =====================================================

}
