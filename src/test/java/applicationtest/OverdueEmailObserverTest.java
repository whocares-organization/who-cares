package applicationtest;

import application.EmailService;
import application.LoanService;
import application.OverdueEmailObserver;
import domain.Book;
import domain.Loan;
import domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests overdue notification via Observer pattern using a mocked EmailService.
 */
class OverdueEmailObserverTest {

    private LoanService loanService;
    private EmailService emailService;

    // ================= Setup & Teardown =================
    @BeforeEach
    void setUp() {
        // Clear repositories for isolated test scenario
        BookRepository.clearBooks();
        LoanRepository.clearLoans();
        MemberRepository.clearMembers();

        emailService = mock(EmailService.class);
        loanService = new LoanService();
        loanService.addObserver(new OverdueEmailObserver(emailService));
    }
    // ====================================================

    // ================= Overdue Notification Tests =================
    @Test
    void scanAndNotifyOverduesWithNewOverdueLoan_ShouldSendSingleEmail() {
        String userEmail = "s12218103@stu.najah.edu";
        String isbn = "OBS-ISBN-001";

        MemberRepository.addMember(new Member("id1", userEmail, "pass"));
        BookRepository.addBook(new Book("Observer Demo Book", "Author", isbn));

        Loan loan = loanService.borrow(isbn, userEmail);
        // Force overdue state
        loan.setDueDate(LocalDate.now().minusDays(2));
        loan.calculateFine(LocalDate.now());

        // First scan should notify
        loanService.scanAndNotifyOverdues(LocalDate.now());
        // Second scan should NOT notify again
        loanService.scanAndNotifyOverdues(LocalDate.now().plusDays(1));

        // Verify single invocation
        verify(emailService, times(1)).sendEmail(eq(userEmail), anyString(), anyString());

        // Capture arguments for content verification
        ArgumentCaptor<String> subjectCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmail(eq(userEmail), subjectCap.capture(), bodyCap.capture());

        String subject = subjectCap.getValue();
        String body = bodyCap.getValue();

        assertTrue(subject.contains("Overdue") || subject.contains("Library"), "Subject should reference overdue context");
        assertTrue(body.contains(isbn), "Body should contain ISBN");
        assertTrue(body.contains(userEmail), "Body should contain member email");
        assertTrue(body.matches(".*fine.*\\d+(\\.\\d+)?"), "Body should mention fine amount");
    }
    // ===============================================================
}