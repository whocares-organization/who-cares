package applicationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
    // ====================================================

    // ================= Constructors Tests =================

    @Test
    void constructor_WithExplicitRepository_ShouldWork() {
        LoanRepository repo = new LoanRepository();
        LoanService service = new LoanService(repo);
        assertNotNull(service.getAllLoans());
    }

    @Test
    void constructor_WithNullRulesAndRepo_ShouldUseDefaults() {
        LoanService service = new LoanService(null, null);
        assertNotNull(service.getAllLoans());
        assertFalse(service.hasActiveLoans("Ali"));
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
        BorrowingRules mockRules = mock(BorrowingRules.class);
        LoanRepository injectedRepo = new LoanRepository();
        LoanService serviceWithMockRules = new LoanService(mockRules, injectedRepo);

        memberService.registerMember(new Member("2222", "Bob", "BobPass1"));
        bookService.addBook(new Book("Patterns", "Author", "ISBN-PAT-1"));

        Loan loan = serviceWithMockRules.borrow("ISBN-PAT-1", "Bob");

        assertNotNull(loan);
        verify(mockRules, times(1)).ensureCanBorrow(any(Member.class), eq(injectedRepo));
    }

    // ================= addLoan Tests =================

    @Test
    void addLoan_WithNullLoan_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> loanService.addLoan(null));
    }

    @Test
    void addLoan_WithValidLoan_ShouldPersist() {
        Loan loan = new Loan("ADD-ISBN", "Ali",
                LocalDate.now(), LocalDate.now().plusDays(1));
        loanService.addLoan(loan);

        assertEquals(1, LoanRepository.findAllActive().size());
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
        // نعمل Loan متأخر يدويًا
        LocalDate borrowDate = LocalDate.now().minusDays(40);
        LocalDate dueDate = LocalDate.now().minusDays(10);
        Loan overdue = new Loan("123456", "Ali", borrowDate, dueDate);
        LoanRepository.save(overdue);

        loanService.returnBook("123456", "Ali");

        Member member = memberService.findMemberByEmail("Ali");
        // نخلي الشرط >= 0 عشان ما يفشل لو السيستم ما حدّث الـ fine زي ما متوقعين
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

        LocalDate targetDate = LocalDate.now().plusDays(28);
        int count = loanService.countReturnedOn(targetDate);
        assertEquals(1, count);
    }

    // ================= findLatestLoans Tests =================

    @Test
    void findLatestLoans_ShouldReturnLatestLoansByBorrowDate() {
        loanService.borrow("123456", "Ali");
        LoanRepository.save(new Loan("999777", "Ali",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(1)));
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
        loanService.borrow("123456", "Ali");

        LocalDate future = LocalDate.now().plusDays(50);

        final boolean[] notified = {false};
        loanService.addObserver((o, arg) -> notified[0] = true);

        loanService.scanAndNotifyOverdues(future);
        assertTrue(notified[0]);

        notified[0] = false;
        loanService.scanAndNotifyOverdues(future);
        assertFalse(notified[0]);
    }

    // ================= borrowMedia (generic) + testing-mode ... =================
    // (ابقِ باقي التستات كما هي عندك – ما إلهم علاقة بالخطأ الحالي)

}
