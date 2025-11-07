package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan transaction between a library member and a book or other media.
 *
 * <p>This class stores all relevant information about a loan, including
 * the media’s ID (or ISBN for books), the member’s ID, the borrow and due dates, whether
 * the media has been returned, and any fines accrued for overdue returns.</p>
 */
public class Loan {

    // Backward-compatible identifiers
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

    // Flag to ensure observers are notified only once per overdue transition
    private boolean overdueNotificationSent;

    /** The media associated with this loan, if any. */
    private Media media;

    /**
     * Default constructor that creates an empty {@code Loan} object.
     */
    public Loan() {}

    /**
     * Constructs a new {@code Loan} with the specified media details.
     * The loan is initialized as not returned and with zero fine.
     * The due date is calculated based on the media's borrow period.
     *
     * @param media the media being borrowed
     * @param memberId the ID of the member borrowing the media
     * @param borrowDate the date when the media was borrowed
     */
    public Loan(Media media, String memberId, LocalDate borrowDate) {
        this.media = media;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returned = false;
        this.fineAmount = 0;
        this.overdueNotificationSent = false;
        if (media != null) {
            this.isbn = media.getId(); // mirror for compatibility
            this.dueDate = borrowDate.plusDays(media.getBorrowPeriod());
            media.borrowAt(borrowDate);
        }
    }

    /**
     * Constructs a new {@code Loan} with the specified details.
     * The loan is initialized as not returned and with zero fine.
     *
     * @param isbn the ID of the borrowed media (ISBN for books)
     * @param memberId the ID of the member borrowing the media
     * @param borrowDate the date when the media was borrowed
     * @param dueDate the date when the media is due to be returned
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
     * Gets the ID of the borrowed media.
     *
     * @return the media’s ID (or ISBN for books)
     */
    public String getIsbn() {
        return isbn != null ? isbn : (media != null ? media.getId() : null);
    }

    /**
     * Sets the ID of the borrowed media.
     *
     * @param isbn the media’s ID (or ISBN for books)
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the ID of the member who borrowed the media.
     *
     * @return the member ID
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Sets the ID of the member who borrowed the media.
     *
     * @param memberId the member ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * Gets the date when the media was borrowed.
     *
     * @return the borrow date
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the date when the media was borrowed.
     *
     * @param borrowDate the borrow date
     */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the date when the media is due for return.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the date when the media is due for return.
     *
     * @param dueDate the due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Checks if the media has been returned.
     *
     * @return {@code true} if the media has been returned, {@code false} otherwise
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Sets the return status of the loan.
     *
     * @param returned {@code true} if the media has been returned, {@code false} otherwise
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Checks if the loan is overdue based on the given date.
     *
     * @param today the current date
     * @return {@code true} if the media is overdue and not yet returned, {@code false} otherwise
     */
    public boolean isOverdue(LocalDate today) {
        return !returned && dueDate != null && today.isAfter(dueDate);
    }

    /**
     * Calculates the fine for overdue media based on the number of days late.
     * <p>The fine is calculated using the media's fine per day rate, or 0.5 if not applicable.</p>
     *
     * @param today the current date
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
     * Gets the total fine amount for the loan.
     *
     * @return the fine amount
     */
    public double getFineAmount() {
        return fineAmount;
    }

    /**
     * Sets the total fine amount for the loan.
     *
     * @param fineAmount the fine amount
     */
    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    /**
     * Returns a string representation of the loan with its details.
     *
     * @return a string describing the loan
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

    // Added accessor methods for observer logic
    public boolean isOverdueNotificationSent() {
        return overdueNotificationSent;
    }

    public void markOverdueNotificationSent() {
        this.overdueNotificationSent = true;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
}