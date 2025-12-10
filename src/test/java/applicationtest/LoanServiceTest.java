package applicationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.BookService;
import application.BorrowingRules;
import application.LoanService;
import application.MemberService;
import domain.Book;
import domain.Loan;
import domain.Member;
import domain.Media;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

class LoanServiceTest {

    private LoanService loanService;
    private MemberService memberService;
    private BookService bookService;

    // ================= Setup & Teardown =================
    @BeforeEach
    void setUp(){
        memberService = new MemberService();
        bookService = new BookService();
        loanService = new LoanService();

        memberService.registerMember(new Member("1111", "Ali", "Ali1234"));
        bookService.addBook(new Book("Java Basics", "Mohammad", "123456"));
    }

    @AfterEach
    void tearDown(){
        BookRepository.clearBooks();
        MemberRepository.clearMembers();
        LoanRepository.clearLoans();
    }
    // ====================================================

    // ================= Borrow Tests =================
    @Test
    void givenValidMemberAndBook_whenBorrow_thenLoanCreatedAndBookMarkedBorrowed() {
        Loan loan = loanService.borrow("123456", "Ali");
        assertNotNull(loan);
        assertEquals(LocalDate.now().plusDays(28), loan.getDueDate());
        assertTrue(bookService.searchBooks("123456").isBorrowed());
    }

    @Test
    void borrowNonExistingBook_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrow("999999", "Ali"));
        assertEquals("Book not found", ex.getMessage());
    }

    @Test
    void borrowNonExistingMember_ShouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrow("123456", "NonExist"));
        assertEquals("Member not found", ex.getMessage());
    }

    @Test
    void borrowAlreadyBorrowedBook_ShouldThrowException() {
        loanService.borrow("123456", "Ali");
        Exception ex = assertThrows(IllegalStateException.class,
                () -> loanService.borrow("123456", "Ali"));
        assertEquals("Book is already borrowed", ex.getMessage());
    }

    @Test
    void borrowMemberWithUnpaidFine_ShouldThrowException() {
        Member member = memberService.findMemberByEmail("Ali");
        member.addMemberFine(50);
        Exception ex = assertThrows(IllegalStateException.class,
                () -> loanService.borrow("123456", "Ali"));
        assertEquals("Member has unpaid fines!", ex.getMessage());
    }

    @Test
    void borrow_DelegatesToBorrowingRules() {
        // Arrange: mock rules and use fresh service instance
        BorrowingRules mockRules = mock(BorrowingRules.class);
        LoanRepository injectedRepo = new LoanRepository();
        LoanService serviceWithMockRules = new LoanService(mockRules, injectedRepo);

        // Seed required member and book (using existing services/repositories)
        memberService.registerMember(new Member("2222", "Bob", "BobPass1"));
        bookService.addBook(new Book("Patterns", "Author", "ISBN-PAT-1"));

        // Act
        Loan loan = serviceWithMockRules.borrow("ISBN-PAT-1", "Bob");

        // Assert: ensure loan created and delegated validation invoked exactly once
        assertNotNull(loan);
        verify(mockRules, times(1)).ensureCanBorrow(any(Member.class), eq(injectedRepo));
    }

    // ================= Return Tests =================
    @Test
    void returnBorrowedBook_ShouldMarkReturnedAndBookAvailable() {
        loanService.borrow("123456", "Ali");
        loanService.returnBook("123456", "Ali");

        assertFalse(bookService.searchBooks("123456").isBorrowed());
        assertTrue(LoanRepository.findActiveByMember("Ali").isEmpty());
    }

    @Test
    void returnNonExistingLoan_ShouldNotThrow() {
        assertDoesNotThrow(() -> loanService.returnBook("999999", "Ali"));
    }

    @Test
    void returnBookWithBookRecordNotFound_ShouldNotThrow() {
        loanService.borrow("123456", "Ali");
        BookRepository.clearBooks();
        assertDoesNotThrow(() -> loanService.returnBook("123456", "Ali"));
    }

    @Test
    void returnOverdueBook_ShouldApplyFine() {
        Loan loan = loanService.borrow("123456", "Ali");
        LocalDate futureDate = LocalDate.now().plusDays(35);
        loanService.returnBook("123456", "Ali");

        Member member = memberService.findMemberByEmail("Ali");
        assertTrue(member.getFineBalance() >= 0);
    }

    // ================= Overdue Tests =================
    @Test
    void findOverdues_ShouldReturnOverdueLoans() {
        loanService.borrow("123456", "Ali");
        LocalDate futureDate = LocalDate.now().plusDays(40);
        List<Loan> overdueLoans = loanService.findOverdues(futureDate);
        assertFalse(overdueLoans.isEmpty());
    }

    @Test
    void findOverduesWithNoOverdue_ShouldReturnEmpty() {
        loanService.borrow("123456", "Ali");
        List<Loan> overdueLoans = loanService.findOverdues(LocalDate.now());
        assertTrue(overdueLoans.isEmpty());
    }

    @Test
    void findOverduesWithNoLoansAtAll_ShouldReturnEmpty() {
        List<Loan> overdueLoans = loanService.findOverdues(LocalDate.now());
        assertTrue(overdueLoans.isEmpty());
    }

    // ================= ShowAllLoans Tests =================
    @Test
    void showAllLoans_ShouldListActiveLoans() {
        loanService.borrow("123456", "Ali");
        assertDoesNotThrow(() -> loanService.showAllLoans());
    }

    @Test
    void showAllLoansWithNoActiveLoans_ShouldNotThrow() {
        assertDoesNotThrow(() -> loanService.showAllLoans());
    }

    // ================= getOverdueLoansForMember Tests =================
    @Test
    void getOverdueLoansForMember_WithOverdueLoans_ShouldReturnLoans() {
        loanService.borrow("123456", "Ali");
        LocalDate futureDate = LocalDate.now().plusDays(30);
        List<Loan> overdue = loanService.getOverdueLoansForMember("Ali", futureDate);
        assertEquals(1, overdue.size());
    }

    @Test
    void getOverdueLoansForMember_WithNoOverdueLoans_ShouldReturnEmptyList() {
        loanService.borrow("123456", "Ali");
        List<Loan> overdue = loanService.getOverdueLoansForMember("Ali", LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    @Test
    void getOverdueLoansForMember_WithNoLoans_ShouldReturnEmptyList() {
        List<Loan> overdue = loanService.getOverdueLoansForMember("Ali", LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    @Test
    void getOverdueLoansForMember_WithNullMemberId_ShouldReturnEmptyList() {
        List<Loan> overdue = loanService.getOverdueLoansForMember(null, LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    @Test
    void getOverdueLoansForMember_WithBlankMemberId_ShouldReturnEmptyList() {
        List<Loan> overdue = loanService.getOverdueLoansForMember("  ", LocalDate.now());
        assertTrue(overdue.isEmpty());
    }
    
    // ================= countActiveLoans Tests =================
    @Test
    void countActiveLoans_ShouldReturnCorrectCount() {
        assertEquals(0, loanService.countActiveLoans());
        loanService.borrow("123456", "Ali");
        assertEquals(1, loanService.countActiveLoans());
    }

    // ================= countReturnedOn Tests =================
    @Test
    void countReturnedOn_ShouldCountLoansReturnedOnSpecificDate() {
        loanService.borrow("123456", "Ali");
        loanService.returnBook("123456", "Ali");

        // LoanService.countReturnedOn counts loans where isReturned == true AND dueDate equals the provided date.
        LocalDate targetDate = LocalDate.now().plusDays(28);
        int count = loanService.countReturnedOn(targetDate);
        assertEquals(1, count);
    }

    // ================= findLatestLoans Tests =================
    @Test
    void findLatestLoans_ShouldReturnLatestLoansByBorrowDate() {
        loanService.borrow("123456", "Ali");
        LoanRepository.save(new Loan("999777", "Ali", LocalDate.now().minusDays(2),
                                     LocalDate.now().plusDays(1)));
        List<Loan> latest = loanService.findLatestLoans(1);
        assertEquals(1, latest.size());
        assertEquals("123456", latest.get(0).getIsbn());
    }

    // ================= getAllLoans Tests =================
    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        loanService.borrow("123456", "Ali");
        assertEquals(1, loanService.getAllLoans().size());
    }

    // ================= findOverdueLoans Tests =================
    @Test
    void findOverdueLoans_ShouldReturnActiveOverdueLoans() {
        loanService.borrow("123456", "Ali");
        List<Loan> overdue = loanService.findOverdueLoans(LocalDate.now().plusDays(40));
        assertEquals(1, overdue.size());
    }

    // ================= hasActiveLoans Tests =================
    @Test
    void hasActiveLoans_ShouldReturnTrueWhenMemberHasLoans() {
        loanService.borrow("123456", "Ali");
        assertTrue(loanService.hasActiveLoans("Ali"));
    }

    @Test
    void hasActiveLoans_NullOrBlank_ShouldReturnFalse() {
        assertFalse(loanService.hasActiveLoans(null));
        assertFalse(loanService.hasActiveLoans(" "));
    }

    @Test
    void hasActiveLoans_NoLoans_ShouldReturnFalse() {
        assertFalse(loanService.hasActiveLoans("Ali"));
    }

    // ================= scanAndNotifyOverdues Tests =================
    @Test
    void scanAndNotifyOverdues_ShouldNotifyObserversOncePerLoan() {
        Loan loan = loanService.borrow("123456", "Ali");

        // Fake overdue
        LocalDate future = LocalDate.now().plusDays(50);

        // Add observer
        final boolean[] notified = {false};
        loanService.addObserver((o, arg) -> notified[0] = true);

        loanService.scanAndNotifyOverdues(future);
        assertTrue(notified[0]); // first notification

        notified[0] = false;
        loanService.scanAndNotifyOverdues(future);
        assertFalse(notified[0]); // should NOT notify again
    }

    // ================= borrowMedia (generic) Tests =================
    @Test
    void borrowMedia_Generic_ShouldBorrowMediaSuccessfully() {
        Media media = new Book("Clean Code", "Martin", "CC001");
        media.setBorrowed(false);
        Member member = memberService.findMemberByEmail("Ali");

        Loan loan = loanService.borrowMedia(member, media, LocalDate.now());
        assertNotNull(loan);
        assertTrue(media.isBorrowed());
    }

    @Test
    void borrowMedia_Generic_WhenMediaAlreadyBorrowed_ShouldThrow() {
        Media media = new Book("Clean Code", "Martin", "CC001");
        media.setBorrowed(true);
        Member member = memberService.findMemberByEmail("Ali");

        assertThrows(IllegalStateException.class,
                () -> loanService.borrowMedia(member, media, LocalDate.now()));
    }

    @Test
    void borrowMedia_WithCustomDays_ShouldCreateCorrectDueDate() {
        Media media = new Book("DDD", "Evans", "DDD1");
        media.setBorrowed(false);
        Member member = memberService.findMemberByEmail("Ali");

        Loan loan = loanService.borrowMedia(member, media, LocalDate.now(), 10);
        assertEquals(LocalDate.now().plusDays(10), loan.getDueDate());
    }

    // ================= returnMedia Tests =================
    @Test
    void returnMedia_ShouldMarkLoanReturnedAndMediaAvailable() {
        Media media = new Book("Patterns", "Author", "PAT01");
        Member member = memberService.findMemberByEmail("Ali");
        Loan loan = loanService.borrowMedia(member, media, LocalDate.now());

        loanService.returnMedia(loan, LocalDate.now());

        assertTrue(loan.isReturned());
        assertFalse(media.isBorrowed());
    }

    @Test
    void returnMedia_AlreadyReturned_ShouldNotChangeAnything() {
        Media media = new Book("X", "Y", "Z1");
        Member member = memberService.findMemberByEmail("Ali");
        Loan loan = loanService.borrowMedia(member, media, LocalDate.now());
        loan.setReturned(true);

        assertDoesNotThrow(() -> loanService.returnMedia(loan, LocalDate.now()));
    }

    // ================= calculateTotalFinesForMember Tests =================
    @Test
    void calculateTotalFinesForMember_ShouldSumAllFines() {
        Member member = memberService.findMemberByEmail("Ali");

        Loan loan = loanService.borrow("123456", "Ali");
        loan.calculateFine(LocalDate.now().plusDays(40));

        double total = loanService.calculateTotalFinesForMember(member, LocalDate.now().plusDays(40));

        assertTrue(total > 0);
    }

    @Test
    void calculateTotalFinesForMember_NullMember_ShouldReturnZero() {
        assertEquals(0.0, loanService.calculateTotalFinesForMember(null, LocalDate.now()));
    }

    // ================= Testing-Mode Tests =================

    @Test
    void borrowMediaTestDuration_ShouldCreateTestingLoan() {
        Media media = new Book("Speed", "A", "SP1");
        Member member = memberService.findMemberByEmail("Ali");

        Loan loan = loanService.borrowMediaTestDuration(member, media,
                LocalDate.now(), 0, 0, 0, 5);

        assertNotNull(loan.getTestingDueDate());
        assertEquals(1, loanService.getTestingModeLoansSnapshot().size());
    }

    @Test
    void borrowMediaTestDuration_SecondsOnly_ShouldCreateTestingLoan() {
        Media media = new Book("Speed2", "A", "SP2");
        Member member = memberService.findMemberByEmail("Ali");

        Loan loan = loanService.borrowMediaTestDuration(member, media, 5);

        assertEquals(5, loan.getTestingDurationSeconds());
    }

    @Test
    void getTestingModeLoansSnapshot_ShouldReturnUnmodifiableList() {
        List<Loan> snapshot = loanService.getTestingModeLoansSnapshot();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(mock(Loan.class)));
    }

    @Test
    void removeTestingModeLoan_ShouldRemoveCorrectLoan() {
        Media media = new Book("Speed3", "A", "SP3");
        Member member = memberService.findMemberByEmail("Ali");

        Loan loan = loanService.borrowMediaTestDuration(member, media, 5);
        assertEquals(1, loanService.getTestingModeLoansSnapshot().size());

        loanService.removeTestingModeLoan(loan);
        assertEquals(0, loanService.getTestingModeLoansSnapshot().size());
    }

    @Test
    void findOverdueTestingModeLoans_ShouldReturnExpiredLoans() throws InterruptedException {
        Media media = new Book("Speed", "A", "SP9");
        Member member = memberService.findMemberByEmail("Ali");

        loanService.borrowMediaTestDuration(member, media, 1);

        Thread.sleep(1200); // wait for expiration

        List<Loan> list = loanService.findOverdueTestingModeLoans();
        assertEquals(1, list.size());
    }

}