package applicationtest;

import domain.CD;
import domain.Loan;
import domain.Member;
import org.junit.jupiter.api.*;
import application.CDService;
import persistence.CDRepository;
import persistence.LoanRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CDService and CDRepository covering Sprint 5 CD requirements.
 */
class CDServiceTest {

    private CDService cdService;

    // ================= Setup & Teardown =================
    @BeforeEach
    void setUp() {
        CDRepository.clearCDs();
        LoanRepository.clearLoans();
        cdService = new CDService();
    }

    @AfterEach
    void tearDown() {
        CDRepository.clearCDs();
        LoanRepository.clearLoans();
        cdService = null;
    }
    // ====================================================

    // ================= Borrow CD Tests =================
    @Test
    void borrowCDWithValidMemberAndExistingCD_ShouldSetDueDateAndBorrowedFlag() {
        // Arrange
        CD cd = new CD("CD-101", "Chill Vibes", "Various");
        CDRepository.addCD(cd);
        Member member = new Member("userA@example.com", "pw");
        LocalDate today = LocalDate.now();
        // Act
        Loan loan = cdService.borrowCD(member, "CD-101", today);
        // Assert
        assertNotNull(loan, "Loan should be created");
        assertEquals(today, loan.getBorrowDate(), "Borrow date should match today");
        assertEquals(today.plusDays(7), loan.getDueDate(), "Due date should be +7 days");
        assertTrue(cd.isBorrowed(), "CD should be marked as borrowed");
    }

    @Test
    void borrowCDWithNonExistingCD_ShouldThrow() {
        // Arrange
        Member member = new Member("userB@example.com", "pw");
        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cdService.borrowCD(member, "NO-CD", LocalDate.now()));
        assertTrue(ex.getMessage().contains("CD not found"), "Exception message should mention missing CD");
    }

    @Test
    void borrowCDToday_ShouldSetDueDatePlusSevenDays() {
        // Arrange
        CD cd = new CD("CD-404", "Ocean Waves", "Nature Sounds");
        CDRepository.addCD(cd);
        Member member = new Member("userD@example.com", "pw");
        LocalDate today = LocalDate.now();
        // Act
        Loan loan = cdService.borrowCD(member, "CD-404", today);
        // Assert
        assertEquals(today.plusDays(7), loan.getDueDate(), "Due date should be today + 7 days");
    }
    // ====================================================

    // ================= Return CD Tests =================
    @Test
    void returnCDAfterOverdue_ShouldComputeCorrectFineAndClearBorrowedFlag() {
        // Arrange
        CD cd = new CD("CD-202", "Retro Beats", "DJ Time");
        CDRepository.addCD(cd);
        Member member = new Member("userC@example.com", "pw");
        LocalDate borrowDate = LocalDate.now().minusDays(10); // due 3 days ago
        cdService.borrowCD(member, "CD-202", borrowDate);
        LocalDate returnDate = LocalDate.now();
        // Act
        double fine = cdService.returnCD(member, "CD-202", returnDate);
        // Assert
        assertEquals(3 * 20.0, fine, "Fine should be 3 days * 20 NIS/day");
        assertFalse(cd.isBorrowed(), "CD should no longer be marked as borrowed");
        Loan active = cdService.getActiveLoan(member, "CD-202");
        assertNull(active, "Active loan should no longer exist");
    }
    // ====================================================

    // ================= Delete CD Tests =================
    @Test
    void deleteCDById_ShouldRemoveFromRepository() {
        // Arrange
        CD cd = new CD("CD-303", "Focus Music", "Various");
        CDRepository.addCD(cd);
        assertNotNull(CDRepository.findById("CD-303"), "CD should exist before deletion");
        // Act
        boolean removed = CDRepository.removeById("CD-303");
        // Assert
        assertTrue(removed, "removeById should return true");
        assertNull(CDRepository.findById("CD-303"), "CD should not exist after deletion");
    }
    // ====================================================
    
 // ================= Additional BorrowCD Branch Tests =================

    @Test
    void borrowCD_WithNullMember_ShouldThrow() {
        CD cd = new CD("CD-500", "Test", "Artist");
        CDRepository.addCD(cd);
        assertThrows(IllegalArgumentException.class,
                () -> cdService.borrowCD(null, "CD-500", LocalDate.now()));
    }

    @Test
    void borrowCD_WithBlankCdId_ShouldThrow() {
        Member member = new Member("x@example.com", "pw");
        assertThrows(IllegalArgumentException.class,
                () -> cdService.borrowCD(member, "   ", LocalDate.now()));
    }

    @Test
    void borrowCD_WithNullBorrowDate_ShouldThrow() {
        CD cd = new CD("CD-600", "Audio", "Artist");
        CDRepository.addCD(cd);
        Member member = new Member("y@example.com", "pw");
        assertThrows(IllegalArgumentException.class,
                () -> cdService.borrowCD(member, "CD-600", null));
    }

    @Test
    void borrowCD_WhenCdAlreadyBorrowed_ShouldThrow() {
        CD cd = new CD("CD-700", "Hits", "Artist");
        CDRepository.addCD(cd);
        Member member = new Member("z@example.com", "pw");
        cdService.borrowCD(member, "CD-700", LocalDate.now());
        assertThrows(IllegalStateException.class,
                () -> cdService.borrowCD(member, "CD-700", LocalDate.now()));
    }
    
 // ================= Additional ReturnCD Branch Tests =================

    @Test
    void returnCD_WithNullMember_ShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> cdService.returnCD(null, "CD-800", LocalDate.now()));
    }

    @Test
    void returnCD_WithBlankCdId_ShouldThrow() {
        Member member = new Member("aa@example.com", "pw");
        assertThrows(IllegalArgumentException.class,
                () -> cdService.returnCD(member, "   ", LocalDate.now()));
    }

    @Test
    void returnCD_WithNullReturnDate_ShouldThrow() {
        Member member = new Member("bb@example.com", "pw");
        assertThrows(IllegalArgumentException.class,
                () -> cdService.returnCD(member, "CD-900", null));
    }

    @Test
    void returnCD_WhenActiveLoanNotFound_ShouldThrow() {
        Member member = new Member("cc@example.com", "pw");
        assertThrows(IllegalArgumentException.class,
                () -> cdService.returnCD(member, "CD-404-NO-LOAN", LocalDate.now()));
    }

    @Test
    void returnCD_CdNotFoundInRepository_ShouldStillReturnFine() {
        CD cd = new CD("CD-1000", "Soft", "Artist");
        CDRepository.addCD(cd);
        Member m = new Member("dd@example.com", "pw");
        LocalDate borrow = LocalDate.now().minusDays(7);
        cdService.borrowCD(m, "CD-1000", borrow);

        CDRepository.removeById("CD-1000");

        double fine = cdService.returnCD(m, "CD-1000", LocalDate.now());
        assertTrue(fine >= 0.0);
    }
    
    @Test
    void getActiveLoan_WithNullMemberOrCdId_ShouldReturnNull() {
        assertNull(cdService.getActiveLoan(null, "CD-X"));
        Member m = new Member("ee@example.com", "pw");
        assertNull(cdService.getActiveLoan(m, null));
    }

    @Test
    void previewFine_NoActiveLoan_ShouldReturnZero() {
        Member m = new Member("ff@example.com", "pw");
        assertEquals(0.0, cdService.previewFine(m, "CD-ZZ", LocalDate.now()));
    }

    @Test
    void previewFine_WithNullToday_ShouldReturnZero() {
        Member m = new Member("gg@example.com", "pw");
        CD cd = new CD("CD-1111", "TestAudio", "Artist");
        CDRepository.addCD(cd);
        cdService.borrowCD(m, "CD-1111", LocalDate.now());
        assertEquals(0.0, cdService.previewFine(m, "CD-1111", null));
    }

    @Test
    void previewFine_WhenNotOverdue_ShouldReturnZero() {
        Member m = new Member("hh@example.com", "pw");
        CD cd = new CD("CD-2222", "Calm", "Artist");
        CDRepository.addCD(cd);
        LocalDate borrow = LocalDate.now().minusDays(3);
        cdService.borrowCD(m, "CD-2222", borrow);
        assertEquals(0.0, cdService.previewFine(m, "CD-2222", LocalDate.now()));
    }



    
    
}