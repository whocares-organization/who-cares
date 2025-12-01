package applicationtest;

import application.CDService;
import domain.CD;
import domain.Loan;
import domain.Member;
import persistence.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
}
