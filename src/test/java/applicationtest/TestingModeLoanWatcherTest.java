package applicationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        Loan loan = loanService.borrowMediaTestDuration(m, b, LocalDate.now(), 0, 0, 0, 1);
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
        verify(emailService, atLeastOnce())
                .sendEmail(eq("member@example.com"), contains("TESTING MODE"), anyString());
    }

    // ===================== Constructor validation =====================

    @Test
    void constructor_ShouldThrow_WhenLoanServiceNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestingModeLoanWatcher(null, emailService));
    }

    @Test
    void constructor_ShouldThrow_WhenEmailServiceNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestingModeLoanWatcher(loanService, null));
    }

    @Test
    void constructor_WithNonPositivePollInterval_ShouldNotThrow() {
        assertDoesNotThrow(() -> new TestingModeLoanWatcher(loanService, emailService, 0));
        assertDoesNotThrow(() -> new TestingModeLoanWatcher(loanService, emailService, -100));
    }

    // ===================== start / stop branches =====================

    @Test
    void stop_WhenNotStarted_ShouldNotThrow() {
        TestingModeLoanWatcher watcher = new TestingModeLoanWatcher(loanService, emailService);
        assertDoesNotThrow(watcher::stop);
    }

    @Test
    void start_WhenAlreadyRunning_ShouldNotStartAgain() {
        TestingModeLoanWatcher watcher = new TestingModeLoanWatcher(loanService, emailService, 50);
        watcher.start();
        // second call should hit the 'already running' branch and simply return
        watcher.start();
        watcher.stop();
    }

    // ===================== run() branch coverage =====================

    @Test
    void run_ShouldSkipEmailAndRemoval_WhenMemberMissing() {
        LoanService mockLoanService = mock(LoanService.class);
        EmailService mockEmail = mock(EmailService.class);

        Loan fakeLoan = mock(Loan.class);
        when(fakeLoan.getMemberId()).thenReturn("missing@example.com");
        when(fakeLoan.getIsbn()).thenReturn("ISBN-X");

        when(mockLoanService.findOverdueTestingModeLoans()).thenAnswer(invocation -> {
            // Interrupt current thread so that sleep() will throw InterruptedException
            Thread.currentThread().interrupt();
            return List.of(fakeLoan);
        });

        TestingModeLoanWatcher watcher = new TestingModeLoanWatcher(mockLoanService, mockEmail, 1000);

        watcher.run();

        Thread.interrupted();

        verify(mockEmail, never()).sendEmail(anyString(), anyString(), anyString());
        verify(mockLoanService, never()).removeTestingModeLoan(any(Loan.class));
    }

    @Test
    void run_WhenLoanServiceThrowsException_ShouldHandleAndContinue() {
        LoanService mockLoanService = mock(LoanService.class);
        EmailService mockEmail = mock(EmailService.class);

        AtomicInteger counter = new AtomicInteger(0);

        when(mockLoanService.findOverdueTestingModeLoans()).thenAnswer(invocation -> {
            int c = counter.getAndIncrement();
            if (c == 0) {
                throw new RuntimeException("boom");
            } else {
                Thread.currentThread().interrupt();
                return Collections.emptyList();
            }
        });

        TestingModeLoanWatcher watcher = new TestingModeLoanWatcher(mockLoanService, mockEmail, 10);

        watcher.run();

        Thread.interrupted();

        verify(mockEmail, never()).sendEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void ctor_WithNullLoanService_ShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestingModeLoanWatcher(null, emailService));
    }
    
    @Test
    void ctor_WithNullEmailService_ShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestingModeLoanWatcher(loanService, null));
    }

    @Test
    void start_WhenAlreadyRunning_ShouldNotCreateNewThread() throws Exception {
        TestingModeLoanWatcher w = new TestingModeLoanWatcher(loanService, emailService, 1000);
        w.start();

        var f = TestingModeLoanWatcher.class.getDeclaredField("thread");
        f.setAccessible(true);
        Thread first = (Thread) f.get(w);

        w.start();
        Thread second = (Thread) f.get(w);

        assertSame(first, second);
        w.stop();
    }

    @Test
    void stop_WhenThreadNull_ShouldNotThrow() {
        TestingModeLoanWatcher w = new TestingModeLoanWatcher(loanService, emailService, 1000);
        assertDoesNotThrow(w::stop);
    }

 


}
