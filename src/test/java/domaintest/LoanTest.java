package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
