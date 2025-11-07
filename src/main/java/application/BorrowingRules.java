package application;

import domain.Loan;
import domain.Member;
import persistence.LoanRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Encapsulates business rules governing whether a member may borrow a new book.
 * Pure domain/service logic: no email, no UI.
 */
public class BorrowingRules {

    /**
     * Determines whether a member can borrow:
     *  - Member must have no unpaid fines (member.canBorrow() == true)
     *  - Member must not have any overdue active loans
     *
     * @param member the member attempting to borrow
     * @param loanRepository repository to inspect active overdue loans
     * @return true if borrowing is allowed, false otherwise
     */
    public boolean canBorrow(Member member, LoanRepository loanRepository) {
        if (member == null) return false;
        if (!member.canBorrow()) return false; // unpaid fines present
        List<Loan> overdue = loanRepository.findActiveOverdueByMember(member.getUserName(), LocalDate.now());
        return overdue.isEmpty();
    }

    /**
     * Ensures a member can borrow or throws an IllegalStateException with a specific reason.
     *
     * @param member the member attempting to borrow
     * @param loanRepository repository to inspect overdue loans
     */
    public void ensureCanBorrow(Member member, LoanRepository loanRepository) {
        if (member == null) {
            throw new IllegalStateException("Member not found!");
        }
        if (!member.canBorrow()) {
            throw new IllegalStateException("Member has unpaid fines!");
        }
        List<Loan> overdue = loanRepository.findActiveOverdueByMember(member.getUserName(), LocalDate.now());
        if (!overdue.isEmpty()) {
            throw new IllegalStateException("Member has overdue loans!");
        }
    }
}
