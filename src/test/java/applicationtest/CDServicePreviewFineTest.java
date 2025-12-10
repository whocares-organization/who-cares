package applicationtest;

import application.CDService;
import domain.CD;
import domain.Loan;
import domain.Member;
import persistence.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CDService.previewFine and LoanRepository behavior.
 */
class CDServicePreviewFineTest {

    private CDService service;
    private Member member;
    private CD cd;

    @BeforeEach
    void setup() {
        // reset in-memory repo before each test
        LoanRepository.clearLoans();
        service = new CDService();
        member = new Member();
        member.setUserName("john.doe@example.com");
        cd = new CD("CD-123", "Some Album", "Some Artist");
    }

    // ======================= previewFine Tests =======================

    @Test
    void previewFine_ReturnsZero_WhenNoActiveLoan() {
        double fine = service.previewFine(member, cd.getId(), LocalDate.now());
        assertEquals(0.0, fine, 0.0001);
    }

    @Test
    void previewFine_ReturnsZero_WhenTodayIsNull() {
        // create an active loan
        Loan loan = new Loan(cd, member.getUserName(), LocalDate.now());
        LoanRepository.save(loan);
        double fine = service.previewFine(member, cd.getId(), null);
        assertEquals(0.0, fine, 0.0001);
    }

    @Test
    void previewFine_ReturnsZero_WhenNotOverdue() {
        LocalDate borrowDate = LocalDate.of(2025, 11, 1);
        Loan loan = new Loan(cd, member.getUserName(), borrowDate);
        LoanRepository.save(loan);
        // due date is borrowDate + 7, not overdue if today == due date
        LocalDate today = borrowDate.plusDays(cd.getBorrowPeriod());
        double fine = service.previewFine(member, cd.getId(), today);
        assertEquals(0.0, fine, 0.0001);
    }

    @Test
    void previewFine_ComputesFine_WhenOverdue() {
        LocalDate borrowDate = LocalDate.of(2025, 10, 1);
        Loan loan = new Loan(cd, member.getUserName(), borrowDate);
        LoanRepository.save(loan);
        // overdue by 3 days
        LocalDate today = borrowDate.plusDays(cd.getBorrowPeriod() + 3);
        double fine = service.previewFine(member, cd.getId(), today);
        // CD fine per day = 20.0
        assertEquals(3 * cd.getFinePerDay(), fine, 0.0001);
    }

    // ======================= LoanRepository Direct Tests =======================

    @Test
    void loanRepositoryConstructor_ShouldCreateInstance() {
        LoanRepository repo = new LoanRepository();
        assertNotNull(repo, "LoanRepository instance should be created");
    }

    @Test
    void saveLoan_ShouldAppearInFindAll() {
        Loan loan = mock(Loan.class);
        LoanRepository.save(loan);

        List<Loan> all = LoanRepository.findAll();
        assertTrue(all.contains(loan), "Saved loan should appear in findAll");
    }

    @Test
    void findAllActive_ShouldReturnOnlyNonReturnedLoans() {
        Loan active = mock(Loan.class);
        when(active.isReturned()).thenReturn(false);

        Loan returned = mock(Loan.class);
        when(returned.isReturned()).thenReturn(true);

        LoanRepository.save(active);
        LoanRepository.save(returned);

        List<Loan> activeLoans = LoanRepository.findAllActive();
        assertTrue(activeLoans.contains(active), "Active loan should be included");
        assertFalse(activeLoans.contains(returned), "Returned loan should not be included");
    }

    @Test
    void findActiveByIsbn_ShouldReturnOnlyMatchingNonReturnedLoan() {
        Loan activeMatching = mock(Loan.class);
        when(activeMatching.isReturned()).thenReturn(false);
        when(activeMatching.getIsbn()).thenReturn("ISBN-1");

        Loan activeOtherIsbn = mock(Loan.class);
        when(activeOtherIsbn.isReturned()).thenReturn(false);
        when(activeOtherIsbn.getIsbn()).thenReturn("ISBN-2");

        Loan returnedSameIsbn = mock(Loan.class);
        when(returnedSameIsbn.isReturned()).thenReturn(true);
        when(returnedSameIsbn.getIsbn()).thenReturn("ISBN-1");

        LoanRepository.save(activeMatching);
        LoanRepository.save(activeOtherIsbn);
        LoanRepository.save(returnedSameIsbn);

        Loan found = LoanRepository.findActiveByIsbn("ISBN-1");
        assertSame(activeMatching, found, "Should return non-returned loan with matching ISBN");
    }

    @Test
    void findActiveByMember_ShouldReturnOnlyNonReturnedLoansOfMember() {
        Loan memberActive = mock(Loan.class);
        when(memberActive.isReturned()).thenReturn(false);
        when(memberActive.getMemberId()).thenReturn("M1");

        Loan otherMemberActive = mock(Loan.class);
        when(otherMemberActive.isReturned()).thenReturn(false);
        when(otherMemberActive.getMemberId()).thenReturn("M2");

        Loan memberReturned = mock(Loan.class);
        when(memberReturned.isReturned()).thenReturn(true);
        when(memberReturned.getMemberId()).thenReturn("M1");

        LoanRepository.save(memberActive);
        LoanRepository.save(otherMemberActive);
        LoanRepository.save(memberReturned);

        List<Loan> loans = LoanRepository.findActiveByMember("M1");
        assertEquals(1, loans.size());
        assertSame(memberActive, loans.get(0));
    }

    @Test
    void markReturned_ShouldCallSetReturnedTrueOnLoan() {
        Loan loan = mock(Loan.class);
        LoanRepository.markReturned(loan);
        verify(loan).setReturned(true);
    }

    @Test
    void remove_ShouldRemoveLoanFromRepository() {
        Loan loan = mock(Loan.class);
        LoanRepository.save(loan);
        assertTrue(LoanRepository.findAll().contains(loan));

        LoanRepository.remove(loan);
        assertFalse(LoanRepository.findAll().contains(loan), "Loan should be removed");
    }

    @Test
    void clearLoans_ShouldEmptyRepository() {
        Loan loan = mock(Loan.class);
        LoanRepository.save(loan);
        assertFalse(LoanRepository.findAll().isEmpty(), "Precondition: repository not empty");

        LoanRepository.clearLoans();
        assertTrue(LoanRepository.findAll().isEmpty(), "After clearLoans, repository should be empty");
    }

    @Test
    void findActiveByMemberAndIsbn_ShouldReturnMatchingActiveLoan() {
        Loan matching = mock(Loan.class);
        when(matching.isReturned()).thenReturn(false);
        when(matching.getMemberId()).thenReturn("M1");
        when(matching.getIsbn()).thenReturn("ISBN-1");

        Loan diffMember = mock(Loan.class);
        when(diffMember.isReturned()).thenReturn(false);
        when(diffMember.getMemberId()).thenReturn("M2");
        when(diffMember.getIsbn()).thenReturn("ISBN-1");

        Loan sameMemberDifferentIsbn = mock(Loan.class);
        when(sameMemberDifferentIsbn.isReturned()).thenReturn(false);
        when(sameMemberDifferentIsbn.getMemberId()).thenReturn("M1");
        when(sameMemberDifferentIsbn.getIsbn()).thenReturn("ISBN-2");

        Loan returnedSameAll = mock(Loan.class);
        when(returnedSameAll.isReturned()).thenReturn(true);
        when(returnedSameAll.getMemberId()).thenReturn("M1");
        when(returnedSameAll.getIsbn()).thenReturn("ISBN-1");

        LoanRepository.save(matching);
        LoanRepository.save(diffMember);
        LoanRepository.save(sameMemberDifferentIsbn);
        LoanRepository.save(returnedSameAll);

        Loan found = LoanRepository.findActiveByMemberAndIsbn("M1", "ISBN-1");
        assertSame(matching, found);
    }

    @Test
    void findAllActiveOverdue_ShouldReturnOnlyNonReturnedOverdueLoans() {
        LocalDate today = LocalDate.now();

        Loan overdue = mock(Loan.class);
        when(overdue.isReturned()).thenReturn(false);
        when(overdue.isOverdue(today)).thenReturn(true);

        Loan notOverdue = mock(Loan.class);
        when(notOverdue.isReturned()).thenReturn(false);
        when(notOverdue.isOverdue(today)).thenReturn(false);

        Loan returnedOverdue = mock(Loan.class);
        when(returnedOverdue.isReturned()).thenReturn(true);
        when(returnedOverdue.isOverdue(today)).thenReturn(true);

        LoanRepository.save(overdue);
        LoanRepository.save(notOverdue);
        LoanRepository.save(returnedOverdue);

        List<Loan> result = LoanRepository.findAllActiveOverdue(today);
        assertEquals(1, result.size());
        assertSame(overdue, result.get(0));
    }

    @Test
    void findActiveOverdueByMember_WithNullMemberId_ShouldReturnEmptyList() {
        LocalDate today = LocalDate.now();

        Loan overdue = mock(Loan.class);
        when(overdue.isReturned()).thenReturn(false);
        when(overdue.getMemberId()).thenReturn("M1");
        when(overdue.isOverdue(today)).thenReturn(true);

        LoanRepository.save(overdue);

        LoanRepository repo = new LoanRepository();
        List<Loan> result = repo.findActiveOverdueByMember(null, today);
        assertTrue(result.isEmpty(), "Null memberId should result in empty list");
    }

    @Test
    void findActiveOverdueByMember_ShouldReturnOverdueLoansForMember() {
        LocalDate today = LocalDate.now();

        Loan overdueMember1 = mock(Loan.class);
        when(overdueMember1.isReturned()).thenReturn(false);
        when(overdueMember1.getMemberId()).thenReturn("M1");
        when(overdueMember1.isOverdue(today)).thenReturn(true);

        Loan notOverdueMember1 = mock(Loan.class);
        when(notOverdueMember1.isReturned()).thenReturn(false);
        when(notOverdueMember1.getMemberId()).thenReturn("M1");
        when(notOverdueMember1.isOverdue(today)).thenReturn(false);

        Loan overdueOtherMember = mock(Loan.class);
        when(overdueOtherMember.isReturned()).thenReturn(false);
        when(overdueOtherMember.getMemberId()).thenReturn("M2");
        when(overdueOtherMember.isOverdue(today)).thenReturn(true);

        Loan returnedOverdueMember1 = mock(Loan.class);
        when(returnedOverdueMember1.isReturned()).thenReturn(true);
        when(returnedOverdueMember1.getMemberId()).thenReturn("M1");
        when(returnedOverdueMember1.isOverdue(today)).thenReturn(true);

        LoanRepository.save(overdueMember1);
        LoanRepository.save(notOverdueMember1);
        LoanRepository.save(overdueOtherMember);
        LoanRepository.save(returnedOverdueMember1);

        LoanRepository repo = new LoanRepository();
        List<Loan> result = repo.findActiveOverdueByMember("M1", today);

        assertEquals(1, result.size());
        assertSame(overdueMember1, result.get(0));
    }
}
