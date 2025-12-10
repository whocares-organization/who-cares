package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Book;
import domain.Loan;

class LoanTest {

    private Loan loan;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        loan = new Loan("123456", "user1", today.minusDays(10), today.plusDays(5));
    }

    @Test
    void testDefaultConstructor() {
        Loan l = new Loan();
        assertNotNull(l);
    }

    @Test
    void testGettersAndSetters() {
        loan.setIsbn("654321");
        assertEquals("654321", loan.getIsbn());

        loan.setMemberId("user2");
        assertEquals("user2", loan.getMemberId());

        loan.setBorrowDate(today);
        assertEquals(today, loan.getBorrowDate());

        loan.setDueDate(today.plusDays(7));
        assertEquals(today.plusDays(7), loan.getDueDate());

        loan.setReturned(true);
        assertTrue(loan.isReturned());

        loan.setFineAmount(15.0);
        assertEquals(15.0, loan.getFineAmount());
    }

    @Test
    void testIsOverdue_NotReturned_BeforeDue() {
        assertFalse(loan.isOverdue(today));
    }

    @Test
    void testIsOverdue_NotReturned_AfterDue() {
        loan.setDueDate(today.minusDays(1));
        assertTrue(loan.isOverdue(today));
    }

    @Test
    void testIsOverdue_AlreadyReturned() {
        loan.setReturned(true);
        loan.setDueDate(today.minusDays(5));
        assertFalse(loan.isOverdue(today));
    }

    @Test
    void testCalculateFine_Overdue() {
        loan.setDueDate(today.minusDays(4));
        loan.calculateFine(today);
        assertEquals(4 * 0.5, loan.getFineAmount());
    }

    @Test
    void testCalculateFine_NotOverdue() {
        loan.setDueDate(today.plusDays(3));
        loan.calculateFine(today);
        assertEquals(0.0, loan.getFineAmount());
    }

    @Test
    void testToStringContainsAllFields() {
        String str = loan.toString();
        assertTrue(str.contains("123456"));
        assertTrue(str.contains("user1"));
        assertTrue(str.contains("borrowDate"));
        assertTrue(str.contains("dueDate"));
    }

    // ===================== إضافات لتغطية 100% =====================

    @Test
    void constructorWithMedia_ShouldInitializeFieldsAndDueDate() {
        LocalDate borrow = today;
        Book book = new Book("Title", "Author", "B-001");

        Loan l = new Loan(book, "memberX", borrow);

        assertEquals("B-001", l.getIsbn());
        assertEquals("memberX", l.getMemberId());
        assertEquals(borrow, l.getBorrowDate());
        assertEquals(borrow.plusDays(book.getBorrowPeriod()), l.getDueDate());
        assertFalse(l.isReturned());
        assertEquals(0.0, l.getFineAmount());
        assertEquals(book, l.getMedia());
    }

    @Test
    void getIsbn_UsesMediaIdWhenIsbnNull() {
        LocalDate borrow = today;
        Book book = new Book("T", "A", "MEDIA-ID-1");

        Loan l = new Loan(book, "m1", borrow);
        l.setIsbn(null); // يجبره يستخدم media.getId()

        assertEquals("MEDIA-ID-1", l.getIsbn());
    }

    @Test
    void getIsbn_ReturnsNullWhenNoIsbnAndNoMedia() {
        Loan l = new Loan();
        l.setIsbn(null);
        assertNull(l.getIsbn());
    }

    @Test
    void isOverdue_WithNullDueDate_ShouldReturnFalse() {
        loan.setDueDate(null);
        loan.setReturned(false);
        assertFalse(loan.isOverdue(today));
    }

    @Test
    void calculateFine_WithMedia_ShouldUseMediaFinePerDay() {
        LocalDate borrow = today.minusDays(10);
        Book book = new Book("FineBook", "Author", "FB-1");

        Loan l = new Loan(book, "memberFine", borrow);
        LocalDate due = l.getDueDate();
        LocalDate afterDue = due.plusDays(3); // متأخر 3 أيام

        l.calculateFine(afterDue);

        assertEquals(3 * book.getFinePerDay(), l.getFineAmount(), 0.0001);
    }

    @Test
    void overdueNotificationSentFlag_ShouldToggle() {
        assertFalse(loan.isOverdueNotificationSent());
        loan.markOverdueNotificationSent();
        assertTrue(loan.isOverdueNotificationSent());
    }

    @Test
    void setMedia_ShouldAttachMediaAndGetMediaReturnSameInstance() {
        Loan l = new Loan();
        Book book = new Book("X", "Y", "SET-MEDIA-1");

        l.setMedia(book);

        assertEquals(book, l.getMedia());
    }

    @Test
    void setTestingDurationSeconds_ValidValue_ShouldSetField() {
        Loan l = new Loan();
        l.setTestingDurationSeconds(2);
        assertEquals(2, l.getTestingDurationSeconds());
    }

    @Test
    void setTestingDurationSeconds_NonPositive_ShouldThrow() {
        Loan l = new Loan();
        assertThrows(IllegalArgumentException.class, () -> l.setTestingDurationSeconds(0));
        assertThrows(IllegalArgumentException.class, () -> l.setTestingDurationSeconds(-1));
    }

    @Test
    void getTestingDueDate_ShouldReturnValueSetBySetter() {
        Loan l = new Loan();
        Instant dueAt = Instant.now().plusSeconds(60);
        l.setTestingDueDate(dueAt);

        assertEquals(dueAt, l.getTestingDueDate());
    }

    @Test
    void isTestingDurationExpired_WhenReturned_ShouldReturnFalse() {
        Loan l = new Loan();
        l.setReturned(true);
        l.setTestingDueDate(Instant.now().minusSeconds(10)); // حتى لو منتهي

        assertFalse(l.isTestingDurationExpired());
    }

    @Test
    void isTestingDurationExpired_WithExplicitDueDatePast_ShouldReturnTrue() {
        Loan l = new Loan();
        l.setTestingDueDate(Instant.now().minusSeconds(2));

        assertTrue(l.isTestingDurationExpired());
    }

    @Test
    void isTestingDurationExpired_WithExplicitDueDateFuture_ShouldReturnFalse() {
        Loan l = new Loan();
        l.setTestingDueDate(Instant.now().plusSeconds(5));

        assertFalse(l.isTestingDurationExpired());
    }

    @Test
    void isTestingDurationExpired_WithoutTestingConfig_ShouldReturnFalse() {
        Loan l = new Loan();
        assertFalse(l.isTestingDurationExpired());
    }

    @Test
    void isTestingDurationExpired_WithDurationNotElapsed_ShouldReturnFalse() {
        Loan l = new Loan();
        l.setTestingDurationSeconds(5); 

        assertFalse(l.isTestingDurationExpired());
    }

    @Test
    void isTestingDurationExpired_WithDurationElapsed_ShouldReturnTrue() throws InterruptedException {
        Loan l = new Loan();
        l.setTestingDurationSeconds(1); 
        Thread.sleep(1100);

        assertTrue(l.isTestingDurationExpired());
    }
}
