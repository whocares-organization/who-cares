package domain;

/**
 * Represents a Compact Disc (CD) media item with artist information
 * and specific borrowing/fine policy.
 *
 * <p>CDs can be borrowed for a shorter period than books and incur
 * a higher fine per overdue day.</p>
 */
public class CD extends Media {

    /** The recording artist associated with this CD. */
    private String artist;

    /**
     * Creates a new CD with default values.
     *
     * <p>Calls the {@link Media#Media()} default constructor.</p>
     */
    public CD() { super(); }

    /**
     * Creates a new CD with the given identifier, title, and artist.
     *
     * @param id     the unique identifier for this CD
     * @param title  the title of the CD
     * @param artist the recording artist name
     */
    public CD(String id, String title, String artist) {
        super(id, title);
        this.artist = artist;
    }

    /**
     * Returns the recording artist for this CD.
     *
     * @return the artist name, or {@code null} if not set
     */
    public String getArtist() { return artist; }

    /**
     * Updates the recording artist for this CD.
     *
     * @param artist the new artist name
     */
    public void setArtist(String artist) { this.artist = artist; }

    /**
     * The borrow period for CDs.
     *
     * @return the number of days a CD may be borrowed (7)
     */
    @Override
    public int getBorrowPeriod() { return 7; }

    /**
     * The fine charged per overdue day for CDs.
     *
     * @return the fine amount per day (20.0)
     */
    @Override
    public double getFinePerDay() { return 20.0; }
}