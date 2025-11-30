package application;

import domain.CD;
import domain.Loan;
import domain.Member;
import persistence.CDRepository;
import persistence.LoanRepository;

import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Application layer service for Compact Discs (CD) handling borrow/return operations
 * and overdue fine calculation. This class focuses ONLY on CD business rules:
 *
 * <p>Borrow period: 7 days.<br>Overdue fine: 20 NIS per day (applied once the due date has passed).</p>
 * It delegates persistence concerns to {@link CDRepository} and {@link LoanRepository}.
 */
public class CDService {

    private static final Logger LOGGER = Logger.getLogger(CDService.class.getName());

    /**
     * Creates a new {@code CDService} instance.
     */
    public CDService() { }

    /**
     * Borrows a CD by its id for the given member starting on borrowDate.
     * Creates and stores a {@link Loan} instance bound to the CD and member.
     * @param member the borrowing member (must not be null and must be allowed to borrow)
     * @param cdId the CD identifier (must exist in repository)
     * @param borrowDate the start date of borrowing
     * @return the created Loan object
     * @throws IllegalArgumentException if arguments are null or CD does not exist
     * @throws IllegalStateException if member cannot borrow or CD already borrowed
     */
    public Loan borrowCD(Member member, String cdId, LocalDate borrowDate) {
        if (member == null) throw new IllegalArgumentException("Member is required.");
        if (cdId == null || cdId.isBlank()) throw new IllegalArgumentException("CD id is required.");
        if (borrowDate == null) throw new IllegalArgumentException("Borrow date is required.");

        CD cd = CDRepository.findById(cdId);
        if (cd == null) throw new IllegalArgumentException("CD not found: " + cdId);
        if (cd.isBorrowed()) throw new IllegalStateException("CD is already borrowed.");
        new BorrowingRules().ensureCanBorrow(member, new LoanRepository());

        Loan loan = new Loan(cd, member.getUserName(), borrowDate);
        LoanRepository.save(loan);
        LOGGER.info(() -> "CD borrowed: " + cdId + " by " + member.getUserName());
        return loan;
    }

    /**
     * Returns a previously borrowed CD, computing any overdue fine at the given returnDate.
     * @param member the member returning the CD
     * @param cdId identifier of the CD to return
     * @param returnDate the date the CD is being returned
     * @return the calculated fine (0 if not overdue)
     * @throws IllegalArgumentException if arguments are null or active loan not found
     */
    public double returnCD(Member member, String cdId, LocalDate returnDate) {
        if (member == null) throw new IllegalArgumentException("Member is required.");
        if (cdId == null || cdId.isBlank()) throw new IllegalArgumentException("CD id is required.");
        if (returnDate == null) throw new IllegalArgumentException("Return date is required.");

        Loan loan = LoanRepository.findActiveByMemberAndIsbn(member.getUserName(), cdId);
        if (loan == null) throw new IllegalArgumentException("Active loan not found for CD: " + cdId);

        loan.calculateFine(returnDate);
        double fine = loan.getFineAmount();
        loan.setReturned(true);
        CD cd = CDRepository.findById(cdId);
        if (cd != null) cd.markReturned();
        LOGGER.info(() -> "CD returned: " + cdId + " by " + member.getUserName() + ", fine=" + fine);
        return fine;
    }

    /**
     * Retrieves the active (non-returned) loan for a given member and CD id.
     * @param member the member
     * @param cdId the cd identifier
     * @return active loan or null if none
     */
    public Loan getActiveLoan(Member member, String cdId) {
        if (member == null || cdId == null) return null;
        return LoanRepository.findActiveByMemberAndIsbn(member.getUserName(), cdId);
    }

    /**
     * Computes the potential overdue fine for a currently active CD loan as of 'today'
     * WITHOUT marking it returned.
     * @param member the member owning the loan
     * @param cdId cd identifier
     * @param today evaluation date
     * @return fine amount or 0.0 if no active loan or not overdue
     */
    public double previewFine(Member member, String cdId, LocalDate today) {
        Loan active = getActiveLoan(member, cdId);
        if (active == null || today == null) return 0.0;
        active.calculateFine(today);
        return active.getFineAmount();
    }
}