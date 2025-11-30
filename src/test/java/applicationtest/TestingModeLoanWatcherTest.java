package applicationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.EmailService;
import application.LoanService;
import application.TestingModeLoanWatcher;
import domain.Book;
import domain.Member;
import domain.Loan;
import persistence.BookRepository;
import persistence.MemberRepository;
import persistence.LoanRepository;

class TestingModeLoanWatcherTest {

    private LoanService loanService;
    private EmailService emailService;

    @BeforeEach
    void setup() {
        BookRepository.clearBooks();
        MemberRepository.clearMembers();
        LoanRepository.clearLoans();
        loanService = new LoanService();
        emailService = mock(EmailService.class);
    }

    @Test
    void watcherSendsEmailAndRemovesExpiredTestingLoan() throws Exception {
        Member m = new Member("member@example.com", "pw");
        MemberRepository.addMember(m);
        Book b = new Book("Title", "Auth", "ISBN1");
        BookRepository.addBook(b);

        // Borrow media in testing mode for 1 second
        Loan loan = loanService.borrowMediaTestDuration(m, b, LocalDate.now(), 0,0,0,1);
        assertFalse(loan.isReturned());
        assertEquals(1, loanService.getTestingModeLoansSnapshot().size());

        TestingModeLoanWatcher watcher = new TestingModeLoanWatcher(loanService, emailService, 50); // poll quickly
        watcher.start();

        // Wait up to 2 seconds for watcher to process expiration
        long start = System.currentTimeMillis();
        boolean removed = false;
        while (System.currentTimeMillis() - start < 2000) {
            if (loanService.getTestingModeLoansSnapshot().isEmpty()) {
                removed = true;
                break;
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
        watcher.stop();

        assertTrue(removed, "Loan should be removed after expiration");
        verify(emailService, atLeastOnce()).sendEmail(eq("member@example.com"), contains("TESTING MODE"), anyString());
    }
}
