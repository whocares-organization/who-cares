package persistence;

import domain.Loan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class responsible for managing {@link Loan} entities in-memory.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Store and retrieve loan records.</li>
 *   <li>Centralize search and filtering logic for loans.</li>
 * </ul>
 *
 * <p><b>Note:</b> In-memory, non-thread-safe; data is lost when the app stops.</p>
 */
public class LoanRepository {

    /** Internal in-memory storage for all loans. */
    private static ArrayList<Loan> loans = new ArrayList<>();

    /**
     * Persists a new loan.
     *
     * @param loan the {@link Loan} to save
     */
    public static void save(Loan loan) {
        loans.add(loan);
    }

    /**
     * Retrieves all active (non-returned) loans.
     *
     * @return list of active loans (never null)
     */
    public static List<Loan> findAllActive() {
        return loans.stream()
                .filter(l -> !l.isReturned())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all loans (active and returned).
     *
     * @return list of all loans (never null)
     */
    public static List<Loan> findAll() {
        return new ArrayList<>(loans);
    }

    /**
     * Finds the active loan for a specific book ISBN, if any.
     *
     * @param isbn the book ISBN
     * @return matching active loan, or {@code null} if none found
     */
    public static Loan findActiveByIsbn(String isbn) {
        return loans.stream()
                .filter(l -> !l.isReturned() && l.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds all active (non-returned) loans for a given member.
     *
     * @param memberId the member identifier
     * @return list of active loans for the member (never null)
     */
    public static List<Loan> findActiveByMember(String memberId) {
        return loans.stream()
                .filter(l -> !l.isReturned() && l.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    /**
     * Marks the given loan as returned.
     *
     * @param loan the loan to update
     */
    public static void markReturned(Loan loan) {
        loan.setReturned(true);
    }

    /**
     * Removes a loan from the repository.
     *
     * @param loan the loan to remove
     */
    public static void remove(Loan loan) {
        loans.remove(loan);
    }

    /**
     * Clears all loans (useful for tests).
     */
    public static void clearLoans() {
        loans.clear();
    }

    // ========================
    // Centralized query helpers
    // ========================

    /**
     * Finds a single active loan for the given member and ISBN.
     *
     * @param memberId the member identifier
     * @param isbn     the book ISBN
     * @return matching active loan, or {@code null} if none found
     */
    public static Loan findActiveByMemberAndIsbn(String memberId, String isbn) {
        return loans.stream()
                .filter(l -> !l.isReturned()
                        && l.getMemberId().equals(memberId)
                        && l.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds all active loans that are overdue as of the given date.
     *
     * @param today the date used to evaluate overdue status
     * @return list of overdue active loans (never null)
     */
    public static List<Loan> findAllActiveOverdue(LocalDate today) {
        return loans.stream()
                .filter(l -> !l.isReturned() && l.isOverdue(today))
                .collect(Collectors.toList());
    }
}
