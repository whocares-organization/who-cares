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

    protected Media() { }

    protected Media(String id, String title) {
        this.id = id;
        this.title = title;
    }

    // Template attributes -----------------------------------------------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    // Business contract -------------------------------------------------------
    /** Number of days this media can be borrowed. */
    public abstract int getBorrowPeriod();

    /** Fine per overdue day in NIS. */
    public abstract double getFinePerDay();

    // Helpers -----------------------------------------------------------------
    /** Mark this media as borrowed at the given date; due date is set using policy. */
    public void borrowAt(LocalDate borrowDate) {
        this.borrowed = true;
        this.dueDate = borrowDate.plusDays(getBorrowPeriod());
    }

    /** Mark this media as returned (not borrowed) and clear due date. */
    public void markReturned() {
        this.borrowed = false;
        this.dueDate = null;
    }
}
