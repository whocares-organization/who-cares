package applicationtest;

import application.AdminActions;
import application.AdminFileLoader;
import domain.Admin;
import domain.Book;
import domain.CD;
import domain.Loan;
import domain.Member;
import domain.AdminStatus;
import persistence.AdminRepository;
import persistence.LoanRepository;
import persistence.BookRepository;
import persistence.CDRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MediaPolymorphismTest {

    private AdminActions adminActions;
    private Admin admin;

    // ================= Setup & Teardown =================
    @BeforeEach
    void setUp() throws Exception {
        AdminRepository.clearAdmins();
        LoanRepository.clearLoans();
        BookRepository.clearBooks();
        CDRepository.clearCDs();
        adminActions = new AdminActions();
        admin = new AdminFileLoader("admins.txt").loadAdmins().get(0);
        AdminRepository.addAdmin(admin);
        admin.setStatus(AdminStatus.ONLINE);
    }

    @AfterEach
    void tearDown() {
        AdminRepository.clearAdmins();
        LoanRepository.clearLoans();
        BookRepository.clearBooks();
        CDRepository.clearCDs();
        adminActions = null;
        admin = null;
    }
    // ====================================================

    // ================= Borrow Tests =================
    @Test
    void borrowBookToday_ShouldSetDueDatePlus28Days() {
        // Arrange
        Member member = new Member("M-B1", "bookuser@example.com", "pw");
        Book book = new Book("Effective Java", "Joshua Bloch", "ISBN-123");
        BookRepository.addBook(book);
        LocalDate borrowDate = LocalDate.now();
        // Act
        Loan loan = adminActions.borrowMedia(admin, member, book, borrowDate);
        // Assert
        assertEquals(borrowDate.plusDays(28), loan.getDueDate(), "Book due date should be today + 28 days");
        assertTrue(book.isBorrowed(), "Book should be marked as borrowed");
    }

    @Test
    void borrowCDToday_ShouldSetDueDatePlus7Days() {
        // Arrange
        Member member = new Member("M-C1", "cduser@example.com", "pw");
        CD cd = new CD("CD-999", "Top Hits", "Various Artists");
        CDRepository.addCD(cd);
        LocalDate borrowDate = LocalDate.now();
        // Act
        Loan loan = adminActions.borrowMedia(admin, member, cd, borrowDate);
        // Assert
        assertEquals(borrowDate.plusDays(7), loan.getDueDate(), "CD due date should be today + 7 days");
        assertTrue(cd.isBorrowed(), "CD should be marked as borrowed");
        assertEquals(1, CDRepository.findAll().size(), "Repository should contain the CD");
        assertEquals(1, CDRepository.findAllBorrowed().size(), "Repository should show one borrowed CD");
    }
    // ==================================================

    // ================= Fine Calculation Tests =================
    @Test
    void overdueFineForBook_ShouldUse10PerDay() {
        // Arrange
        Member member = new Member("M-B2", "userb2@example.com", "pw");
        Book book = new Book("Clean Code", "Robert C. Martin", "ISBN-456");
        LocalDate borrowDate = LocalDate.now().minusDays(35); // 7 days overdue
        Loan loan = adminActions.borrowMedia(admin, member, book, borrowDate);
        LocalDate today = LocalDate.now();
        // Act
        loan.calculateFine(today);
        // Assert
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), today);
        assertEquals(7, daysLate, "Days late should be 7");
        assertEquals(daysLate * 10.0, loan.getFineAmount(), "Book fine should be 10 NIS/day * days late");
    }

    @Test
    void overdueFineForCD_ShouldUse20PerDay() {
        // Arrange
        Member member = new Member("M-C2", "userc2@example.com", "pw");
        CD cd = new CD("CD-123", "Jazz Classics", "Miles Davis");
        LocalDate borrowDate = LocalDate.now().minusDays(10); // due 3 days ago
        Loan loan = adminActions.borrowMedia(admin, member, cd, borrowDate);
        LocalDate today = LocalDate.now();
        // Act
        loan.calculateFine(today);
        // Assert
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), today);
        assertEquals(daysLate * 20.0, loan.getFineAmount(), "CD fine should be 20 NIS/day * days late");
    }

    @Test
    void mixedMediaFineSummaryAfter40Days_ShouldSumBookAndCDFines() {
        // Arrange
        Member member = new Member("M-MIX", "mixuser@example.com", "pw");
        Book book = new Book("Refactoring", "Martin Fowler", "ISBN-789");
        CD cd = new CD("CD-321", "Rock Legends", "Various");
        LocalDate borrowDate = LocalDate.now();
        adminActions.borrowMedia(admin, member, book, borrowDate);
        adminActions.borrowMedia(admin, member, cd, borrowDate);
        LocalDate today = borrowDate.plusDays(40);
        // Act
        double total = adminActions.calculateMemberFineSummary(admin, member, today);
        // Assert
        long bookDaysLate = java.time.temporal.ChronoUnit.DAYS.between(borrowDate.plusDays(28), today); // 12
        long cdDaysLate = java.time.temporal.ChronoUnit.DAYS.between(borrowDate.plusDays(7), today); // 33
        double expected = bookDaysLate * 10.0 + cdDaysLate * 20.0;
        assertEquals(expected, total, "Total fine should equal sum of book and CD fines");
    }
    // ===========================================================

    // ================= Repository Tests =================
    @Test
    void cdRepositoryFindById_ShouldReturnSameInstance() {
        // Arrange
        CD cd = new CD("CD-321", "Rock Legends", "Various");
        CDRepository.addCD(cd);
        // Act
        CD found = CDRepository.findById("CD-321");
        // Assert
        assertNotNull(found);
        assertSame(cd, found);
    }
    // ====================================================
}