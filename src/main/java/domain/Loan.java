package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.Instant;

/**
 * Represents a loan transaction between a library member and a book or other media.
 *
 * <p>Stores media identifier, member identifier (email), borrow and due dates,
 * return status, fine amount, and testing-mode timing metadata.</p>
 */
public class Loan {

    /** The ID of the borrowed media (ISBN for books). */
    private String isbn;
    /** The ID of the member who borrowed the media. */
    private String memberId;
    /** The date when the media was borrowed. */
    private LocalDate borrowDate;
    /** The date when the media should be returned. */
    private LocalDate dueDate;
    /** Indicates whether the media has been returned. */
    private boolean returned;
    /** The total fine amount for overdue media. */
    private double fineAmount;
    private boolean overdueNotificationSent;
    /** The media associated with this loan, if any. */
    private Media media;
    private long testingStartTimeMillis = 0L;
    private int testingDurationSeconds = 0;
    private Instant testingDueAt;

    /** Default constructor. */
    public Loan() {}

    /**
     * Constructs a loan for a given media and member at a borrow date.
     * The default due date is derived from the media's borrow period.
     *
     * @param media the media being borrowed
     * @param memberId the borrowing member's email/username
     * @param borrowDate the borrow date
     */
    public Loan(Media media, String memberId, LocalDate borrowDate) {
        this.media = media;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returned = false;
        this.fineAmount = 0;
        this.overdueNotificationSent = false;
        if (media != null) {
            this.isbn = media.getId();
            this.dueDate = borrowDate.plusDays(media.getBorrowPeriod());
            media.borrowAt(borrowDate);
        }
    }

    /**
     * Constructs a loan with explicit identifiers and due date.
     *
     * @param isbn the media identifier (ISBN or ID)
     * @param memberId the borrowing member's email/username
     * @param borrowDate the borrow date
     * @param dueDate the normal due date
     */
    public Loan(String isbn, String memberId, LocalDate borrowDate, LocalDate dueDate) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.fineAmount = 0;
        this.overdueNotificationSent = false;
    }

    /**
     * Returns the media identifier for this loan.
     * @return media identifier (ISBN/ID), or {@code null} if unknown
     */
    public String getIsbn() {
        return isbn != null ? isbn : (media != null ? media.getId() : null);
    }

    /**
     * Sets the media identifier.
     * @param isbn media identifier (ISBN/ID)
     */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /**
     * Returns the member identifier used by repositories (email/username).
     * @return member identifier (email/username)
     */
    public String getMemberId() { return memberId; }

    /**
     * Sets the member identifier.
     * @param memberId email/username of the borrowing member
     */
    public void setMemberId(String memberId) { this.memberId = memberId; }

    /**
     * Returns the borrow date.
     * @return borrow date value
     */
    public LocalDate getBorrowDate() { return borrowDate; }

    /**
     * Sets the borrow date.
     * @param borrowDate borrow date value
     */
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    /**
     * Returns the normal due date.
     * @return due date; may be {@code null} for certain flows
     */
    public LocalDate getDueDate() { return dueDate; }

    /**
     * Sets the normal due date.
     * @param dueDate normal due date value
     */
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /**
     * Returns whether the media was returned.
     * @return {@code true} if returned; {@code false} otherwise
     */
    public boolean isReturned() { return returned; }

    /**
     * Marks the loan as returned or not.
     * @param returned {@code true} if returned; {@code false} otherwise
     */
    public void setReturned(boolean returned) { this.returned = returned; }

    /**
     * Indicates if this loan is overdue relative to the given date.
     *
     * @param today current date
     * @return {@code true} if overdue and not returned; {@code false} otherwise
     */
    public boolean isOverdue(LocalDate today) {
        return !returned && dueDate != null && today.isAfter(dueDate);
    }

    /**
     * Computes fine for overdue loans using media fine per day; sets fine to 0 if not overdue.
     * @param today current date
     */
    public void calculateFine(LocalDate today) {
        if (isOverdue(today)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            double perDay = media != null ? media.getFinePerDay() : 0.5;
            fineAmount = daysLate * perDay;
        } else {
            fineAmount = 0;
        }
    }

    /**
     * Returns the current fine amount.
     * @return fine amount value
     */
    public double getFineAmount() { return fineAmount; }

    /**
     * Sets the fine amount.
     * @param fineAmount fine amount value
     */
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    /**
     * String representation of this loan.
     * @return string with key fields
     */
    @Override
    public String toString() {
        return "Loan{" +
                "mediaId='" + getIsbn() + '\'' +
                ", memberId='" + memberId + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returned=" + returned +
                ", fineAmount=" + fineAmount +
                '}';
    }

    /**
     * Returns whether an overdue notification has been sent (observer path).
     * @return {@code true} if notification already sent; {@code false} otherwise
     */
    public boolean isOverdueNotificationSent() { return overdueNotificationSent; }

    /**
     * Marks that an overdue notification was sent once.
     */
    public void markOverdueNotificationSent() { this.overdueNotificationSent = true; }

    /**
     * Returns the associated media instance if available.
     * @return media or {@code null}
     */
    public Media getMedia() { return media; }

    /**
     * Sets the associated media instance.
     * @param media media instance
     */
    public void setMedia(Media media) { this.media = media; }

    /**
     * Sets testing-mode duration (seconds) and captures start time.
     * Intended for short testing flows; does not change normal due date logic.
     *
     * @param seconds testing duration in seconds (> 0)
     * @throws IllegalArgumentException if seconds &le; 0
     */
    public void setTestingDurationSeconds(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Testing duration seconds must be > 0");
        }
        this.testingDurationSeconds = seconds;
        this.testingStartTimeMillis = System.currentTimeMillis();
    }

    /**
     * Returns testing-mode duration in seconds, if set (> 0); otherwise 0.
     * @return testing duration seconds
     */
    public int getTestingDurationSeconds() { return this.testingDurationSeconds; }

    /**
     * Sets explicit testing-mode wall-clock due time. If present, expiry checks
     * will use this timestamp in preference to start/duration fields.
     *
     * @param dueAt testing-mode due timestamp
     */
    public void setTestingDueDate(Instant dueAt) { this.testingDueAt = dueAt; }

    /**
     * Returns explicit testing-mode due timestamp, if set.
     * @return testing due {@link Instant} or {@code null}
     */
    public Instant getTestingDueDate() { return this.testingDueAt; }

    /**
     * Returns true if testing-mode duration has expired.
     * Prefers explicit wall-clock due timestamp when available; otherwise uses start time + duration.
     * The check is independent from the normal due date.
     *
     * @return {@code true} if expired in testing mode; {@code false} otherwise
     */
    public boolean isTestingDurationExpired() {
        if (returned) return false;
        if (testingDueAt != null) {
            return Instant.now().isAfter(testingDueAt);
        }
        if (testingDurationSeconds <= 0 || testingStartTimeMillis <= 0L) {
            return false;
        }
        long elapsedSec = (System.currentTimeMillis() - testingStartTimeMillis) / 1000L;
        return elapsedSec >= testingDurationSeconds;
    }
}