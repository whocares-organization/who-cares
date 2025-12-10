package application;

import domain.Book;
import domain.Loan;
import domain.Media;
import domain.Member;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

/**
 * Service responsible for borrowing, returning, and tracking loans.
 *
 * <p>Coordinates with repositories, applies borrowing rules, and
 * notifies observers about overdue loans.</p>
 */
public class LoanService extends Observable {

    private static final Logger LOGGER = Logger.getLogger(LoanService.class.getName());
    private static final int STANDARD_LOAN_DAYS = 28;

    // New: rules and instance repository for rules checks
    private final BorrowingRules borrowingRules;
    private final LoanRepository loanRepository;

    // In-memory tracking for testing-mode loans (not persisted)
    private final List<Loan> testingModeLoans = Collections.synchronizedList(new ArrayList<>());

    /**
     * Creates a service with default rules and repository.
     */
    public LoanService() {
        this.borrowingRules = new BorrowingRules();
        this.loanRepository = new LoanRepository();
    }

    /**
     * Creates a service with a provided repository and default rules.
     *
     * @param loanRepository repository to use; if null, a default instance is created
     */
    public LoanService(LoanRepository loanRepository) {
        this.borrowingRules = new BorrowingRules();
        this.loanRepository = loanRepository != null ? loanRepository : new LoanRepository();
    }

    /**
     * Creates a service with explicit rules and repository.
     *
     * @param borrowingRules borrowing rules; if null, defaults are used
     * @param loanRepository repository; if null, defaults are used
     */
    public LoanService(BorrowingRules borrowingRules, LoanRepository loanRepository) {
        this.borrowingRules = borrowingRules != null ? borrowingRules : new BorrowingRules();
        this.loanRepository = loanRepository != null ? loanRepository : new LoanRepository();
    }

    /**
     * Borrows a book identified by its ISBN for the given user.
     * Applies rules and saves the loan with a standard due date.
     *
     * @param isbn book identifier
     * @param userName member email/username
     * @return created loan
     */
    public Loan borrow(String isbn, String userName) {
        Member member = getExistingMember(userName);
        borrowingRules.ensureCanBorrow(member, loanRepository);
        Book book = getExistingBook(isbn);
        ensureBookNotAlreadyBorrowed(isbn);
        LocalDate today = LocalDate.now();
        Loan loan = new Loan(isbn, userName, today, today.plusDays(STANDARD_LOAN_DAYS));
        LoanRepository.save(loan);
        book.setBorrowed(true);
        LOGGER.info("Book borrowed successfully! Due date: " + loan.getDueDate());
        return loan;
    }

    /**
     * Saves a loan in the repository.
     *
     * @param loan loan to save; must not be null
     */
    public void addLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null");
        }
        LoanRepository.save(loan);
    }

    /**
     * Returns a borrowed book, marks it as returned, and applies fines if overdue.
     *
     * @param isbn book identifier
     * @param memberId member email/username
     */
    public void returnBook(String isbn, String memberId) {
        Loan loan = LoanRepository.findActiveByMemberAndIsbn(memberId, isbn);
        if (loan == null) {
            LOGGER.info("No active loan found for this book/member.");
            return;
        }
        LoanRepository.markReturned(loan);
        Book book = BookRepository.findBookByIsbn(isbn);
        if (book != null) book.setBorrowed(false);
        LocalDate today = LocalDate.now();
        if (loan.isOverdue(today)) {
            loan.calculateFine(today);
            Member member = MemberRepository.findById(memberId);
            if (member != null) {
                member.addMemberFine(loan.getFineAmount());
                LOGGER.info("Book was overdue. Fine added: " + loan.getFineAmount());
            }
        }
        LOGGER.info("Book returned successfully!");
    }

    /**
     * Finds and applies fines to all overdue loans as of the given date.
     *
     * @param today current date
     * @return list of overdue loans
     */
    public List<Loan> findOverdues(LocalDate today) {
        List<Loan> overdueLoans = LoanRepository.findAllActiveOverdue(today);
        overdueLoans.forEach(loan -> {
            loan.calculateFine(today);
            Member member = MemberRepository.findById(loan.getMemberId());
            if (member != null) {
                member.addMemberFine(loan.getFineAmount());
                LOGGER.info("Fine added to member " + member.getUserName()
                        + ": " + loan.getFineAmount()
                        + " for book ISBN " + loan.getIsbn());
            }
        });
        if (overdueLoans.isEmpty()) {
            LOGGER.info("No overdue books found.");
        }
        return overdueLoans;
    }

    /**
     * Scans active loans for overdues and notifies observers once per loan.
     *
     * @param today current date
     */
    public void scanAndNotifyOverdues(LocalDate today) {
        List<Loan> overdueActive = LoanRepository.findAllActiveOverdue(today);
        for (Loan loan : overdueActive) {
            if (!loan.isOverdueNotificationSent()) {
                loan.calculateFine(today);
                setChanged();
                notifyObservers(loan);
                loan.markOverdueNotificationSent();
            }
        }
    }

    /**
     * Logs all active loans to the console.
     */
    public void showAllLoans() {
        List<Loan> loans = LoanRepository.findAllActive();
        if (loans.isEmpty()) {
            LOGGER.info("No active loans currently.");
            return;
        }
        LOGGER.info("=== Active Loans ===");
        LocalDate today = LocalDate.now();
        loans.forEach(l -> LOGGER.info(
                "ISBN: " + l.getIsbn()
                        + " | Member ID: " + l.getMemberId()
                        + " | Due: " + l.getDueDate()
                        + (l.isOverdue(today) ? " | OVERDUE" : "")
        ));
    }

    /**
     * Returns overdue loans for a specific member as of a date.
     *
     * @param memberId member email/username
     * @param date current date
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoansForMember(String memberId, LocalDate date) {
        if (memberId == null || memberId.isBlank()) return List.of();
        return loanRepository.findActiveOverdueByMember(memberId, date);
    }

    // =========================
    // Validation helpers
    // =========================

    private Member getExistingMember(String userName) {
        Member member = MemberRepository.findMemberByEmail(userName);
        if (member == null) throw new IllegalArgumentException("Member not found");
        return member;
    }

    private Book getExistingBook(String isbn) {
        Book book = BookRepository.findBookByIsbn(isbn);
        if (book == null) throw new IllegalArgumentException("Book not found");
        return book;
    }

    private void ensureBookNotAlreadyBorrowed(String isbn) {
        if (LoanRepository.findActiveByIsbn(isbn) != null) {
            throw new IllegalStateException("Book is already borrowed");
        }
    }

    /**
     * Counts active loans in the repository.
     *
     * @return number of active loans
     */
    public int countActiveLoans() {
        return loanRepository.findAllActive().size();
    }

    /**
     * Counts loans returned exactly on a given date.
     *
     * @param date target date
     * @return count of returned loans
     */
    public int countReturnedOn(LocalDate date) {
        return (int) loanRepository.findAll().stream()
                .filter(l -> l.isReturned() && l.getDueDate().equals(date))
                .count();
    }

    /**
     * Returns the latest loans limited by a count.
     *
     * @param limit maximum number of loans
     * @return latest loans
     */
    public List<Loan> findLatestLoans(int limit) {
        return loanRepository.findAll().stream()
                .sorted((a, b) -> b.getBorrowDate().compareTo(a.getBorrowDate()))
                .limit(limit)
                .toList();
    }

    /**
     * Returns all loans.
     *
     * @return list of loans
     */
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    /**
     * Returns overdue loans as of a given date.
     *
     * @param date current date
     * @return list of overdue loans
     */
    public List<Loan> findOverdueLoans(LocalDate date) {
        return loanRepository.findAllActiveOverdue(date);
    }

    /**
     * Indicates whether a member has active loans.
     *
     * @param memberUserName member email/username
     * @return true if active loans exist
     */
    public boolean hasActiveLoans(String memberUserName) {
        if (memberUserName == null || memberUserName.isBlank()) return false;
        return !loanRepository.findActiveByMember(memberUserName).isEmpty();
    }

    // ========================================================================
    // Borrow Media (generic, used by AdminActions.borrowMedia)
    // ========================================================================

    /**
     * Borrows generic media for a member using default media rules.
     *
     * @param member member entity
     * @param media media entity
     * @param borrowDate borrow date
     * @return created loan
     */
    public Loan borrowMedia(Member member, Media media, LocalDate borrowDate) {
        if (member == null || media == null) {
            throw new IllegalArgumentException("Member and media must be provided.");
        }
        borrowingRules.ensureCanBorrow(member, loanRepository);
        if (media.isBorrowed()) {
            throw new IllegalStateException("Media already borrowed.");
        }
        Loan loan = new Loan(media, member.getUserName(), borrowDate);
        loanRepository.save(loan);
        media.setBorrowed(true);
        LOGGER.info("Media borrowed: " + media.getId() + " by member " + member.getUserName());
        return loan;
    }

    /**
     * Borrows generic media with a custom number of days.
     * If days is null or non-positive, defaults to media borrow period.
     *
     * @param member member entity
     * @param media media entity
     * @param borrowDate borrow date
     * @param customDays optional custom days (>0)
     * @return created loan
     */
    public Loan borrowMedia(Member member, Media media, LocalDate borrowDate, Integer customDays) {
        if (member == null || media == null) {
            throw new IllegalArgumentException("Member and media must be provided.");
        }
        borrowingRules.ensureCanBorrow(member, loanRepository);
        if (media.isBorrowed()) {
            throw new IllegalStateException("Media already borrowed.");
        }
        Loan loan;
        if (customDays == null || customDays <= 0) {
            loan = new Loan(media, member.getUserName(), borrowDate);
        } else {
            LocalDate due = borrowDate.plusDays(customDays);
            loan = new Loan(media.getId(), member.getUserName(), borrowDate, due);
            loan.setMedia(media);
            media.borrowAt(borrowDate);
        }
        loanRepository.save(loan);
        media.setBorrowed(true);
        LOGGER.info("Media borrowed: " + media.getId() + " by member " + member.getUserName() +
                " | days=" + (customDays == null || customDays <= 0 ? media.getBorrowPeriod() : customDays));
        return loan;
    }

    // ========================================================================
    // Return Media (generic, used by AdminActions.returnMedia)
    // ========================================================================

    /**
     * Returns media associated with the loan and marks it returned.
     *
     * @param loan loan to return
     * @param returnDate date of return (currently informational)
     */
    public void returnMedia(Loan loan, LocalDate returnDate) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan must be provided.");
        }
        if (loan.isReturned()) {
            LOGGER.info("Loan already returned.");
            return;
        }
        loan.setReturned(true);
        Media media = loan.getMedia();
        if (media != null) {
            media.markReturned();
        }
        LOGGER.info("Media returned: " + loan.getIsbn());
    }

    // ========================================================================
    // Calculate total fines for a member (used by AdminActions.calculateMemberFineSummary)
    // ========================================================================

    /**
     * Calculates the total fines for all loans belonging to a member.
     *
     * @param member member entity
     * @param today current date
     * @return total fines
     */
    public double calculateTotalFinesForMember(Member member, LocalDate today) {
        if (member == null) return 0.0;
        double total = 0.0;
        for (Loan loan : loanRepository.findAll()) {
            if (loan.getMemberId() != null &&
                    loan.getMemberId().equals(member.getUserName())) {
                loan.calculateFine(today);
                total += loan.getFineAmount();
            }
        }
        return total;
    }

    /**
     * Testing-only borrow with custom days/hours/minutes/seconds.
     * Creates and tracks a testing-mode loan with a wall-clock expiration.
     *
     * @param member member entity
     * @param media media entity
     * @param borrowDate borrow date
     * @param days testing days (>=0)
     * @param hours testing hours (>=0)
     * @param minutes testing minutes (>=0)
     * @param seconds testing seconds (>=0)
     * @return created loan (tracked in testing-mode list)
     */
    public Loan borrowMediaTestDuration(Member member,
                                        Media media,
                                        LocalDate borrowDate,
                                        int days,
                                        int hours,
                                        int minutes,
                                        int seconds) {
        if (member == null || media == null) {
            throw new IllegalArgumentException("Member and media must be provided.");
        }
        if (days < 0 || hours < 0 || minutes < 0 || seconds < 0) {
            throw new IllegalArgumentException("Duration components cannot be negative.");
        }
        boolean allZero = days == 0 && hours == 0 && minutes == 0 && seconds == 0;
        if (allZero) {
            throw new IllegalArgumentException("Testing duration must be greater than zero.");
        }
        borrowingRules.ensureCanBorrow(member, loanRepository);
        if (media.isBorrowed()) {
            throw new IllegalStateException("Media already borrowed.");
        }
        long totalSeconds =
                (long) days * 24 * 60 * 60 +
                        (long) hours * 60 * 60 +
                        (long) minutes * 60 +
                        (long) seconds;
        long daysPortion = totalSeconds / (24 * 60 * 60);
        long leftover = totalSeconds % (24 * 60 * 60);
        LocalDate dueDate = borrowDate.plusDays(daysPortion + (leftover > 0 ? 1 : 0));
        Loan loan = new Loan(media.getId(), member.getUserName(), borrowDate, dueDate);
        loan.setMedia(media);
        media.borrowAt(borrowDate);
        loanRepository.save(loan);
        media.setBorrowed(true);
        synchronized (testingModeLoans) {
            int secondsForWatcher = totalSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalSeconds;
            loan.setTestingDurationSeconds(secondsForWatcher);
            loan.setTestingDueDate(Instant.now().plusSeconds(totalSeconds));
            testingModeLoans.add(loan);
        }
        LOGGER.info("[TESTING MODE] Media borrowed: " + media.getId() +
                " by member " + member.getUserName() +
                " | custom testing duration seconds=" + totalSeconds +
                " | testing due at=" + loan.getTestingDueDate());
        return loan;
    }

    /**
     * Testing-only borrow with a duration expressed in seconds.
     * Creates and tracks a testing-mode loan with a wall-clock expiration.
     *
     * @param member member entity
     * @param media media entity
     * @param seconds testing duration in seconds (>0)
     * @return created loan (tracked in testing-mode list)
     */
    public Loan borrowMediaTestDuration(Member member, Media media, int seconds) {
        if (member == null || media == null) {
            throw new IllegalArgumentException("Member and media must be provided.");
        }
        if (seconds <= 0) {
            throw new IllegalArgumentException("Testing duration seconds must be > 0");
        }
        borrowingRules.ensureCanBorrow(member, loanRepository);
        if (media.isBorrowed()) {
            throw new IllegalStateException("Media already borrowed.");
        }
        LocalDate borrowDate = LocalDate.now();
        Loan loan = new Loan(media, member.getUserName(), borrowDate);
        loan.setTestingDurationSeconds(seconds);
        loan.setTestingDueDate(Instant.now().plusSeconds(seconds));
        loanRepository.save(loan);
        media.setBorrowed(true);
        synchronized (testingModeLoans) {
            testingModeLoans.add(loan);
        }
        LOGGER.info("[TESTING MODE] Media borrowed: " + media.getId() +
                " by member " + member.getUserName() +
                " | testing seconds=" + seconds +
                " | testing due at=" + loan.getTestingDueDate());
        return loan;
    }

    /**
     * Returns a snapshot of active testing-mode loans.
     *
     * @return immutable snapshot of testing loans
     */
    public List<Loan> getTestingModeLoansSnapshot() {
        synchronized (testingModeLoans) {
            return List.copyOf(testingModeLoans);
        }
    }

    /**
     * Removes the provided loan from testing-mode tracking.
     *
     * @param loan loan to remove; ignored if null
     */
    public void removeTestingModeLoan(Loan loan) {
        if (loan == null) {
            return;
        }
        synchronized (testingModeLoans) {
            testingModeLoans.remove(loan);
        }
    }

    /**
     * Finds testing-mode loans whose testing timer has expired.
     *
     * @return list of expired testing-mode loans
     */
    public List<Loan> findOverdueTestingModeLoans() {
        synchronized (testingModeLoans) {
            List<Loan> overdue = new ArrayList<>();
            for (Loan loan : testingModeLoans) {
                if (!loan.isReturned() && loan.isTestingDurationExpired()) {
                    overdue.add(loan);
                }
            }
            return overdue;
        }
    }
}
