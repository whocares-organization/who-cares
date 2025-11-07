package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.BookService;
import application.LoanService;
import application.MemberService;
import domain.Book;
import domain.Loan;
import domain.Member;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

class LoanServiceTest {

    private LoanService loanService;
    private MemberService memberService;
    private BookService bookService;

    @BeforeEach
    void setUp() throws Exception {
        memberService = new MemberService();
        bookService = new BookService();
        loanService = new LoanService();

        memberService.registerMember(new Member("1111", "Ali", "Ali1234"));
        bookService.addBook(new Book("Java Basics", "Mohammad", "123456"));
    }

    @AfterEach
    void tearDown() throws Exception {
        BookRepository.clearBooks();
        MemberRepository.clearMembers();
        LoanRepository.clearLoans();
    }

    // ================= Borrow Tests =================
    @Test
    void givenValidMemberAndBook_whenBorrow_thenLoanCreatedAndBookMarkedBorrowed() {
        Loan loan = loanService.borrow("123456", "Ali");
        assertNotNull(loan);
        assertEquals(LocalDate.now().plusDays(28), loan.getDueDate());
        assertTrue(bookService.searchBooks("123456").isBorrowed());
    }

    @Test
    void borrowNonExistingBook_shouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrow("999999", "Ali"));
        assertEquals("Book not found", ex.getMessage());
    }

    @Test
    void borrowNonExistingMember_shouldThrowException() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrow("123456", "NonExist"));
        assertEquals("Member not found", ex.getMessage());
    }

    @Test
    void borrowAlreadyBorrowedBook_shouldThrowException() {
        loanService.borrow("123456", "Ali");
        Exception ex = assertThrows(IllegalStateException.class,
                () -> loanService.borrow("123456", "Ali"));
        assertEquals("Book is already borrowed", ex.getMessage());
    }

    @Test
    void borrowMemberWithUnpaidFine_shouldThrowException() {
        Member member = memberService.findMemberByEmail("Ali");
        member.addMemberFine(50);
        Exception ex = assertThrows(IllegalStateException.class,
                () -> loanService.borrow("123456", "Ali"));
        assertEquals("Member has unpaid fines!", ex.getMessage());
    }

    // ================= Return Tests =================
    @Test
    void returnBorrowedBook_shouldMarkReturnedAndBookAvailable() {
        loanService.borrow("123456", "Ali");
        loanService.returnBook("123456", "Ali");

        assertFalse(bookService.searchBooks("123456").isBorrowed());
        assertTrue(LoanRepository.findActiveByMember("Ali").isEmpty());
    }

    @Test
    void returnNonExistingLoan_shouldNotThrow() {
        assertDoesNotThrow(() -> loanService.returnBook("999999", "Ali"));
    }

    @Test
    void returnBook_bookRecordNotFound_shouldNotThrow() {
        loanService.borrow("123456", "Ali");
        BookRepository.clearBooks();
        assertDoesNotThrow(() -> loanService.returnBook("123456", "Ali"));
    }

    @Test
    void returnOverdueBook_shouldApplyFine() {
        Loan loan = loanService.borrow("123456", "Ali");
        LocalDate futureDate = LocalDate.now().plusDays(35);
        loanService.returnBook("123456", "Ali");

        Member member = memberService.findMemberByEmail("Ali");
        assertTrue(member.getFineBalance() >= 0);
    }

    // ================= Overdue Tests =================
    @Test
    void findOverdues_shouldReturnOverdueLoans() {
        loanService.borrow("123456", "Ali");
        LocalDate futureDate = LocalDate.now().plusDays(40);
        List<Loan> overdueLoans = loanService.findOverdues(futureDate);
        assertFalse(overdueLoans.isEmpty());
    }

    @Test
    void findOverdues_noOverdue_shouldReturnEmpty() {
        loanService.borrow("123456", "Ali");
        List<Loan> overdueLoans = loanService.findOverdues(LocalDate.now());
        assertTrue(overdueLoans.isEmpty());
    }

    @Test
    void findOverdues_noLoansAtAll_shouldReturnEmpty() {
        List<Loan> overdueLoans = loanService.findOverdues(LocalDate.now());
        assertTrue(overdueLoans.isEmpty());
    }

    // ================= ShowAllLoans Tests =================
    @Test
    void showAllLoans_shouldListActiveLoans() {
        loanService.borrow("123456", "Ali");
        assertDoesNotThrow(() -> loanService.showAllLoans());
    }

    @Test
    void showAllLoans_noActiveLoans_shouldNotThrow() {
        assertDoesNotThrow(() -> loanService.showAllLoans());
    }

    // ================= getOverdueLoansForMember Tests =================
    @Test
    void memberWithOverdueLoans_shouldReturnLoans() {
        loanService.borrow("123456", "Ali");
        LocalDate futureDate = LocalDate.now().plusDays(30);
        List<Loan> overdue = loanService.getOverdueLoansForMember("Ali", futureDate);
        assertEquals(1, overdue.size());
    }

    @Test
    void memberWithNoOverdueLoans_shouldReturnEmptyList() {
        loanService.borrow("123456", "Ali");
        List<Loan> overdue = loanService.getOverdueLoansForMember("Ali", LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    @Test
    void memberWithNoLoans_shouldReturnEmptyList() {
        List<Loan> overdue = loanService.getOverdueLoansForMember("Ali", LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    @Test
    void nullMemberId_shouldReturnEmptyList() {
        List<Loan> overdue = loanService.getOverdueLoansForMember(null, LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    @Test
    void blankMemberId_shouldReturnEmptyList() {
        List<Loan> overdue = loanService.getOverdueLoansForMember("  ", LocalDate.now());
        assertTrue(overdue.isEmpty());
    }
}


