package application;

import domain.Loan;
import domain.Member;
import persistence.LoanRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Encapsulates borrowing eligibility rules for library members.
 *
 * <p>Currently enforces basic constraints such as outstanding fines
 * or other application-specific checks before borrowing is permitted.</p>
 */
public class BorrowingRules {

    /**
     * Creates a new instance of {@code BorrowingRules} with default policy.
     */
    public BorrowingRules() { }

    /**
     * Determines whether a member can borrow:
     *  <ul>
     *    <li>Member must have no unpaid fines (member.canBorrow() == true)</li>
     *    <li>Member must not have any overdue active loans</li>
     *  </ul>
     * @param member the member attempting to borrow
     * @param loanRepository repository to inspect active overdue loans
     * @return true if borrowing is allowed, false otherwise
     */
    public boolean canBorrow(Member member, LoanRepository loanRepository) {
        if (member == null) return false;
        if (!member.canBorrow()) return false;
        List<Loan> overdue = loanRepository.findActiveOverdueByMember(member.getUserName(), LocalDate.now());
        return overdue.isEmpty();
    }

    /**
     * Ensures a member can borrow or throws an IllegalStateException with a specific reason.
     * @param member the member attempting to borrow
     * @param loanRepository repository to inspect overdue loans
     * @throws IllegalStateException if member is null, has fines, or has overdue loans
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
