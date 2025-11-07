package applicationtest;

import application.LoanService;
import application.OverdueEmailObserver;
import application.EmailService;
import domain.Book;
import domain.Loan;
import domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Time manipulation tests for overdue detection/notification using Mockito.
 * We control "today" by passing LocalDate into LoanService methods rather than mocking time statics.
 */
class LoanServiceTimeManipulationTest {

    private LoanService loanService;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
   
        loanService = new LoanService();
        emailService = Mockito.mock(EmailService.class);
        // Register observer to capture notifications
        loanService.addObserver(new OverdueEmailObserver(emailService));
    }

    @Test
    void scanAndNotifyOverdues_sendsEmailOnce_perOverdueLoan() {
        // Arrange: create member with ID and email (username), and a loan that is overdue for "today"
        Member member = new Member("M-001", "user@example.com", "pw");
        MemberRepository.addMember(member);

        Book book = new Book("Test Book", "Author", "ISBN-XYZ");
        BookRepository.addBook(book);

        LocalDate borrowDate = LocalDate.now().minusDays(40); // long ago
        // Create media-aware loan so fine uses book policy (10 NIS/day)
        Loan overdueLoan = new Loan(book, member.getId(), borrowDate);
        // Force its due date to 28 days after borrow (already true via Book policy); ensure not returned
        LoanRepository.save(overdueLoan);

        LocalDate today = borrowDate.plusDays(35); // 7 days overdue

        // Act: first scan should send an email
        loanService.scanAndNotifyOverdues(today);
        // Second scan shouldn't send a duplicate
        loanService.scanAndNotifyOverdues(today.plusDays(1));

        // Assert: one email was sent, with body containing ISBN and some fine amount
        verify(emailService, times(1)).sendEmail(eq(member.getId()), anyString(), anyString());

        ArgumentCaptor<String> subjectCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmail(eq(member.getId()), subjectCap.capture(), bodyCap.capture());

        assertTrue(subjectCap.getValue().toLowerCase().contains("overdue"));
        assertTrue(bodyCap.getValue().contains("ISBN"));
        assertTrue(bodyCap.getValue().contains("ISBN-XYZ"));
    }

    @Test
    void findOverdues_addsFinesBasedOnToday_andReturnsOverdueLoans() {
        // Arrange: one overdue loan using media-based constructor to apply book fine policy (10/day)
        Member member = new Member("M-002", "borrower@example.com", "pw");
        MemberRepository.addMember(member);

        Book book = new Book("Another Book", "Writer", "ISBN-ABC");
        BookRepository.addBook(book);

        LocalDate borrowDate = LocalDate.now().minusDays(50);
        Loan loan = new Loan(book, member.getId(), borrowDate);
        LoanRepository.save(loan);

        LocalDate today = borrowDate.plusDays(60); // 32 days overdue (60 - 28)
        long expectedDaysLate = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), today);

        // Act
        List<Loan> overdueLoans = loanService.findOverdues(today);

        // Assert list contains our loan
        assertEquals(1, overdueLoans.size());
        assertEquals("ISBN-ABC", overdueLoans.get(0).getIsbn());

        // Assert member got fine of daysLate * 10
        assertEquals(expectedDaysLate * 10.0, member.getFineBalance());
    }
}
