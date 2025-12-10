package applicationtest;

import domain.CD;
import domain.Loan;
import domain.Member;
import org.junit.jupiter.api.*;
import application.CDService;
import persistence.CDRepository;
import persistence.LoanRepository;

import java.time.LocalDate;
import java.util.List;

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

    // ================= CDRepository Direct Tests =================

    @Test
    void cdRepositoryConstructor_ShouldCreateInstance() {
        CDRepository repo = new CDRepository();
        assertNotNull(repo, "CDRepository instance should be created");
    }

    @Test
    void addCD_Null_ShouldNotChangeRepositorySize() {
        int before = CDRepository.findAll().size();
        CDRepository.addCD(null);
        int after = CDRepository.findAll().size();
        assertEquals(before, after, "Adding null CD should not change repository size");
    }

    @Test
    void removeCD_Null_ShouldNotChangeRepositorySize() {
        CD cd = new CD("CD-X1", "Some Title", "Some Artist");
        CDRepository.addCD(cd);
        int before = CDRepository.findAll().size();
        CDRepository.removeCD(null);
        int after = CDRepository.findAll().size();
        assertEquals(before, after, "Removing null CD should not change repository size");
    }

    @Test
    void removeCD_ValidCD_ShouldRemoveFromRepository() {
        CD cd = new CD("CD-DEL", "Delete Me", "Unknown");
        CDRepository.addCD(cd);
        assertNotNull(CDRepository.findById("CD-DEL"));
        CDRepository.removeCD(cd);
        assertNull(CDRepository.findById("CD-DEL"), "CD should be removed by removeCD");
    }

    @Test
    void findById_NullId_ShouldReturnNull() {
        CD cd = new CD("CD-1", "Title1", "Artist1");
        CDRepository.addCD(cd);
        assertNull(CDRepository.findById(null), "findById(null) should return null");
    }

    @Test
    void findById_NonExistingId_ShouldReturnNull() {
        CD cd = new CD("CD-2", "Title2", "Artist2");
        CDRepository.addCD(cd);
        assertNull(CDRepository.findById("NO-SUCH-ID"), "Should return null for non-existing ID");
    }

    @Test
    void searchFirst_NullOrEmptyKeyword_ShouldReturnNull() {
        CD cd = new CD("CD-10", "Relax", "ArtistX");
        CDRepository.addCD(cd);

        assertNull(CDRepository.searchFirst(null), "searchFirst(null) should return null");
        assertNull(CDRepository.searchFirst(""), "searchFirst(\"\") should return null");
    }

    @Test
    void searchFirst_TitleMatch_ShouldReturnMatchingCD() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-T1", "Chill Beats", "Someone");
        CDRepository.addCD(cd);

        CD found = CDRepository.searchFirst("chill");
        assertNotNull(found);
        assertEquals("CD-T1", found.getId());
    }

    @Test
    void searchFirst_ArtistMatch_ShouldWorkWhenTitleDoesNotMatch() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-A1", "NoMatchTitle", "Cool Artist");
        CDRepository.addCD(cd);

        CD found = CDRepository.searchFirst("cool");
        assertNotNull(found);
        assertEquals("CD-A1", found.getId());
    }

    @Test
    void searchFirst_IdMatch_ShouldWorkWhenTitleAndArtistDoNotMatch() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-SPECIAL-123", "OtherTitle", "OtherArtist");
        CDRepository.addCD(cd);

        CD found = CDRepository.searchFirst("special-123");
        assertNotNull(found);
        assertEquals("CD-SPECIAL-123", found.getId());
    }

    @Test
    void searchFirst_NoMatch_ShouldReturnNull() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-XX", "SomeTitle", "SomeArtist");
        CDRepository.addCD(cd);

        CD found = CDRepository.searchFirst("zzz-not-found");
        assertNull(found, "Should return null if keyword matches nothing");
    }

    @Test
    void findAll_ShouldReturnSnapshotOfAllCDs() {
        CDRepository.clearCDs();
        CD cd1 = new CD("CD-F1", "Title1", "Artist1");
        CD cd2 = new CD("CD-F2", "Title2", "Artist2");
        CDRepository.addCD(cd1);
        CDRepository.addCD(cd2);

        List<CD> all = CDRepository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(cd1));
        assertTrue(all.contains(cd2));
    }

    @Test
    void clearCDs_ShouldEmptyRepository() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-CLEAR", "To Be Cleared", "Artist");
        CDRepository.addCD(cd);
        assertFalse(CDRepository.findAll().isEmpty(), "Precondition: repository not empty");

        CDRepository.clearCDs();
        assertTrue(CDRepository.findAll().isEmpty(), "After clearCDs, repository should be empty");
    }

    @Test
    void findAllBorrowed_NoBorrowedCDs_ShouldReturnEmptyList() {
        CDRepository.clearCDs();
        CD cd1 = new CD("CD-B1", "T1", "A1");
        CD cd2 = new CD("CD-B2", "T2", "A2");
        CDRepository.addCD(cd1);
        CDRepository.addCD(cd2);

        assertFalse(cd1.isBorrowed());
        assertFalse(cd2.isBorrowed());

        List<CD> borrowed = CDRepository.findAllBorrowed();
        assertNotNull(borrowed);
        assertTrue(borrowed.isEmpty(), "No borrowed CDs expected");
    }

    @Test
    void findAllBorrowed_WithBorrowedCDs_ShouldReturnThem() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-BORROWED", "BorrowedTitle", "BorrowedArtist");
        CDRepository.addCD(cd);
        Member member = new Member("borrower@example.com", "pw");

        cdService.borrowCD(member, "CD-BORROWED", LocalDate.now());
        assertTrue(cd.isBorrowed(), "CD should be marked as borrowed by service");

        List<CD> borrowed = CDRepository.findAllBorrowed();
        assertEquals(1, borrowed.size());
        assertEquals("CD-BORROWED", borrowed.get(0).getId());
    }

    @Test
    void removeById_NonExistingId_ShouldReturnFalseAndKeepOtherCDs() {
        CDRepository.clearCDs();
        CD cd = new CD("CD-EXIST", "Exists", "Artist");
        CDRepository.addCD(cd);

        boolean removed = CDRepository.removeById("NO-EXIST");
        assertFalse(removed, "removeById should return false for non-existing id");
        assertNotNull(CDRepository.findById("CD-EXIST"), "Existing CD should remain");
    }
}
