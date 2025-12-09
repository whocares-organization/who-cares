package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import application.BookService;
import applicationsearchbooks.BookAuthorSearchStrategy;
import applicationsearchbooks.BookIsbnSearchStrategy;
import applicationsearchbooks.BookSearchStrategy;
import applicationsearchbooks.BookTitleSearchStrategy;
import persistence.BookRepository;
import domain.Book;

class BookServiceTest {
    
    private BookService bookService;
    private BookRepository repository;

    // ================= Setup & Teardown =================
    @BeforeEach
    void setUp() throws Exception {
        repository = new BookRepository(); 
        bookService = new BookService(repository);
        bookService.addBook(new Book("Olds", "Majd", "12345"));
    }

    @AfterEach
    void tearDown() throws Exception {
        repository.clearBooks();
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

        assertEquals(2, repository.getBooks().size(), "Repository should contain 2 books after adding");
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
    
    //===================== Test A Strategy Pattern For Searching Books ========================
    // ======================= Title Search =======================
    @Test
    void whenSearchByExistingTitle_thenReturnMatchingBook() {
        BookSearchStrategy strategy = new BookTitleSearchStrategy();
        bookService.getRepository().setSearchStrategy(strategy);
        List<Book> result = bookService.search("Olds");

        assertEquals(1, result.size());
        assertEquals("Olds", result.get(0).getTitle());
    }

    @Test
    void whenSearchByNonExistingTitle_thenReturnEmptyList() {
        BookSearchStrategy strategy = new BookTitleSearchStrategy();
        bookService.getRepository().setSearchStrategy(strategy);
        List<Book> result = bookService.search("NonExistingTitle");

        assertTrue(result.isEmpty(), "Should return empty list if title not found");
    }

    // ======================= Author Search =======================
    @Test
    void whenSearchByExistingAuthor_thenReturnMatchingBook() {
        BookSearchStrategy strategy = new BookAuthorSearchStrategy();
        bookService.getRepository().setSearchStrategy(strategy);
        List<Book> result = bookService.search("Majd");

        assertEquals(1, result.size());
        assertEquals("Majd", result.get(0).getAuthor());
    }

    @Test
    void whenSearchByNonExistingAuthor_thenReturnEmptyList() {
        BookSearchStrategy strategy = new BookAuthorSearchStrategy();
        bookService.getRepository().setSearchStrategy(strategy);
        List<Book> result = bookService.search("NonExistingAuthor");

        assertTrue(result.isEmpty(), "Should return empty list if author not found");
    }

    // ======================= ISBN Search =======================
    @Test
    void whenSearchByExistingIsbn_thenReturnMatchingBook() {
        BookSearchStrategy strategy = new BookIsbnSearchStrategy();
        bookService.getRepository().setSearchStrategy(strategy);
        List<Book> result = bookService.search("12345");

        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).getIsbn());
    }

    @Test
    void whenSearchByNonExistingIsbn_thenReturnEmptyList() {
        BookSearchStrategy strategy = new BookIsbnSearchStrategy();
        bookService.getRepository().setSearchStrategy(strategy);
        List<Book> result = bookService.search("999");

        assertTrue(result.isEmpty(), "Should return empty list if ISBN not found");
    }
  //===================== Test A Strategy Pattern For Searching Books ========================


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
    
    @Test
    void whenSetRepository_thenRepositoryIsUpdated() {
        BookRepository newRepository = new BookRepository();
        bookService.setRepository(newRepository);
        assertSame(newRepository, bookService.getRepository(),
                "setRepository should update the internal repository reference");
   }

   
    

}
