package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan transaction between a library member and a book.
 *
 * <p>This class stores all relevant information about a loan, including
 * the book’s ISBN, the member’s ID, the borrow and due dates, whether
 * the book has been returned, and any fines accrued for overdue returns.</p>
 */
public class Loan {

    /** The ISBN of the borrowed book. */
    private String isbn;

    /** The ID of the member who borrowed the book. */
    private String memberId;

    /** The date when the book was borrowed. */
    private LocalDate borrowDate;

    /** The date when the book should be returned. */
    private LocalDate dueDate;

    /** Indicates whether the book has been returned. */
    private boolean returned;

    /** The total fine amount for overdue books. */
    private double fineAmount;

    /**
     * Default constructor that creates an empty {@code Loan} object.
     */
    public Loan() {}

    /**
     * Constructs a new {@code Loan} with the specified details.
     * The loan is initialized as not returned and with zero fine.
     *
     * @param isbn the ISBN of the borrowed book
     * @param memberId the ID of the member borrowing the book
     * @param borrowDate the date when the book was borrowed
     * @param dueDate the date when the book is due to be returned
     */
    public Loan(String isbn, String memberId, LocalDate borrowDate, LocalDate dueDate) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.fineAmount = 0;
    }

    /**
     * Gets the ISBN of the borrowed book.
     *
     * @return the book’s ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the borrowed book.
     *
     * @param isbn the book’s ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the ID of the member who borrowed the book.
     *
     * @return the member ID
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Sets the ID of the member who borrowed the book.
     *
     * @param memberId the member ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * Gets the date when the book was borrowed.
     *
     * @return the borrow date
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the date when the book was borrowed.
     *
     * @param borrowDate the borrow date
     */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the date when the book is due for return.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the date when the book is due for return.
     *
     * @param dueDate the due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Checks if the book has been returned.
     *
     * @return {@code true} if the book has been returned, {@code false} otherwise
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Sets the return status of the loan.
     *
     * @param returned {@code true} if the book has been returned, {@code false} otherwise
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Checks if the loan is overdue based on the given date.
     *
     * @param today the current date
     * @return {@code true} if the book is overdue and not yet returned, {@code false} otherwise
     */
    public boolean isOverdue(LocalDate today) {
        return !returned && today.isAfter(dueDate);
    }

    /**
     * Calculates the fine for overdue books based on the number of days late.
     * <p>The fine is calculated at a rate of 0.5 per day of delay.</p>
     *
     * @param today the current date
     */
    public void calculateFine(LocalDate today) {
        if (isOverdue(today)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            fineAmount = daysLate * 0.5;
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
                "isbn='" + isbn + '\'' +
                ", memberId='" + memberId + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returned=" + returned +
                ", fineAmount=" + fineAmount +
                '}';
    }
}
