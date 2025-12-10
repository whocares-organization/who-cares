/*package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Book;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
    }

    @Test
    void testDefaultConstructorAndSettersGetters() {
        book.setTitle("Java Basics");
        book.setAuthor("Mohammad");
        book.setIsbn("123456");
        book.setBorrowed(true);

        assertEquals("Java Basics", book.getTitle());
        assertEquals("Mohammad", book.getAuthor());
        assertEquals("123456", book.getIsbn());
        assertTrue(book.isBorrowed());
    }

    @Test
    void testConstructorTitleAuthorIsbn() {
        Book b = new Book("Python Basics", "Ali", "654321");
        assertEquals("Python Basics", b.getTitle());
        assertEquals("Ali", b.getAuthor());
        assertEquals("654321", b.getIsbn());
        assertFalse(b.isBorrowed());
    }

    @Test
    void testConstructorWithBorrowedFlag() {
        Book b = new Book("C++ Basics", "Sara", "111222", true);
        assertEquals("C++ Basics", b.getTitle());
        assertEquals("Sara", b.getAuthor());
        assertEquals("111222", b.getIsbn());
        assertTrue(b.isBorrowed());
    }

    @Test
    void testSetBorrowedFalse() {
        book.setBorrowed(true);
        book.setBorrowed(false);
        assertFalse(book.isBorrowed());
    }

    @Test
    void testToStringContainsFields() {
        book.setTitle("Java Basics");
        book.setAuthor("Mohammad");
        book.setIsbn("123456");

        String str = book.toString();
        assertTrue(str.contains("Java Basics"));
        assertTrue(str.contains("Mohammad"));
        assertTrue(str.contains("123456"));
        assertTrue(str.contains("Book"));
    }
}*/
