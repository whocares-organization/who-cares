package domain;

import java.time.LocalDate;

/**
 * Abstract base class for any borrowable media in the library (e.g., Book, CD).
 */
public abstract class Media {

    private String id;          // e.g., ISBN for books, catalog code for CDs
    private String title;
    private boolean borrowed;
    private LocalDate dueDate;

    /**
     * Default protected constructor.
     * Intended for subclass initialization.
     */
    protected Media() { }

    /**
     * Constructs a media item with id and title.
     *
     * @param id the unique identifier (e.g., ISBN)
     * @param title the display title
     */
    protected Media(String id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Returns the identifier of this media.
     * @return the id value
     */
    public String getId() { return id; }

    /**
     * Updates the identifier of this media.
     * @param id the new id value
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the title of this media.
     * @return the title value
     */
    public String getTitle() { return title; }

    /**
     * Updates the title of this media.
     * @param title the new title value
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Indicates whether this media is currently borrowed.
     * @return {@code true} if borrowed; {@code false} otherwise
     */
    public boolean isBorrowed() { return borrowed; }

    /**
     * Updates the borrowed flag.
     * @param borrowed {@code true} if borrowed; {@code false} otherwise
     */
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    /**
     * Returns the due date for the current borrow, if any.
     * @return the due date or {@code null} if not borrowed
     */
    public LocalDate getDueDate() { return dueDate; }

    /**
     * Updates the due date for the current borrow.
     * @param dueDate the new due date to set
     */
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /**
     * Number of days this media can be borrowed.
     * @return the borrow period in days
     */
    public abstract int getBorrowPeriod();

    /**
     * Fine per overdue day in NIS.
     * @return the fine per day value
     */
    public abstract double getFinePerDay();

    /**
     * Marks this media as borrowed at the given date; due date is set using policy.
     *
     * @param borrowDate the date the item was borrowed
     */
    public void borrowAt(LocalDate borrowDate) {
        this.borrowed = true;
        this.dueDate = borrowDate.plusDays(getBorrowPeriod());
    }

    /**
     * Marks this media as returned (not borrowed) and clears the due date.
     */
    public void markReturned() {
        this.borrowed = false;
        this.dueDate = null;
    }
}