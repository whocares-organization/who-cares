package applicationtest;

import application.BorrowingRules;
import domain.Loan;
import domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.LoanRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BorrowingRules encapsulating borrowing eligibility logic (no mocks).
 */
class BorrowingRulesTest {

    private BorrowingRules rules;
    private LoanRepository repo;
    private Member member;

    // ================= Setup & Teardown =================
    @BeforeEach
    void setUp() {
        rules = new BorrowingRules();
        repo = new LoanRepository();
        LoanRepository.clearLoans();
        member = new Member("id1", "user@example.com", "pw");
    }
    // ====================================================

    // ================= canBorrow Tests =================
    @Test
    void canBorrowWithNoFinesAndNoOverdues_ShouldReturnTrue() {
        assertTrue(rules.canBorrow(member, repo));
    }

    @Test
    void canBorrowWithUnpaidFines_ShouldReturnFalse() {
        member.addMemberFine(5.0);
        assertFalse(rules.canBorrow(member, repo));
    }

    @Test
    void canBorrowWithOverdueLoans_ShouldReturnFalse() {
        Loan overdue = new Loan("ISBN1", member.getUserName(), LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        LoanRepository.save(overdue);
        assertFalse(rules.canBorrow(member, repo));
    }
    // ====================================================

    // ================= ensureCanBorrow Tests =================
    @Test
    void ensureCanBorrowWithNullMember_ShouldThrow() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> rules.ensureCanBorrow(null, repo));
        assertEquals("Member not found!", ex.getMessage());
    }

    @Test
    void ensureCanBorrowWithUnpaidFines_ShouldThrow() {
        member.addMemberFine(2.0);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> rules.ensureCanBorrow(member, repo));
        assertEquals("Member has unpaid fines!", ex.getMessage());
    }

    @Test
    void ensureCanBorrowWithOverdueLoans_ShouldThrow() {
        Loan overdue = new Loan("ISBN2", member.getUserName(), LocalDate.now().minusDays(12), LocalDate.now().minusDays(3));
        LoanRepository.save(overdue);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> rules.ensureCanBorrow(member, repo));
        assertEquals("Member has overdue loans!", ex.getMessage());
    }

    @Test
    void ensureCanBorrowAllGood_ShouldNotThrow() {
        assertDoesNotThrow(() -> rules.ensureCanBorrow(member, repo));
    }
    // =========================================================
}