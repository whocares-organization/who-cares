package persistence;

import domain.Loan;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class responsible for managing {@link Loan} entities in memory.
 *
 * <p>This class acts as a simple in-memory database that stores, retrieves,
 * updates, and removes loan records. It supports operations to find active loans,
 * filter by member or book, and mark loans as returned.</p>
 *
 * <p><b>Note:</b> Data stored here is not persistent and will be lost when the
 * application terminates.</p>
 */
public class LoanRepository {

    /** A list that stores all loan records currently in the system. */
    private static final ArrayList<Loan> loans = new ArrayList<>();

    /**
     * Saves a new loan record to the repository.
     *
     * @param loan the {@link Loan} object to be saved
     */
    public static void save(Loan loan) {
        loans.add(loan);
    }

    /**
     * Retrieves all active (non-returned) loans from the repository.
     *
     * @return a list of active {@link Loan} objects
     */
    public static List<Loan> findAllActive() {
        return loans.stream()
                .filter(l -> !l.isReturned())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all loans, both active and returned.
     *
     * @return a list containing all {@link Loan} objects
     */
    public static List<Loan> findAll() {
        return new ArrayList<>(loans);
    }

    /**
     * Finds an active (non-returned) loan associated with a specific book ISBN.
     *
     * @param isbn the ISBN of the book to search for
     * @return the matching active {@link Loan}, or {@code null} if none exists
     */
    public static Loan findActiveByIsbn(String isbn) {
        return loans.stream()
                .filter(l -> !l.isReturned() && l.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all active loans for a specific member.
     *
     * @param memberId the ID of the member
     * @return a list of active {@link Loan} objects associated with the member
     */
    public static List<Loan> findActiveByMember(String memberId) {
        return loans.stream()
                .filter(l -> !l.isReturned() && l.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    /**
     * Marks a specific loan as returned.
     *
     * @param loan the {@link Loan} object to be marked as returned
     */
    public static void markReturned(Loan loan) {
        loan.setReturned(true);
    }

    /**
     * Removes a loan record from the repository.
     *
     * @param loan the {@link Loan} object to remove
     */
    public static void remove(Loan loan) {
        loans.remove(loan);
    }

    /**
     * Clears all loan records from the repository.
     *
     * <p>This operation deletes all loans (active and returned).</p>
     */
    public static void clear() {
        loans.clear();
    }
}
