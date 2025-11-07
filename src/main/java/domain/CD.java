package domain;

/**
 * Compact Disc media type with its own borrowing and fine policy.
 */
public class CD extends Media {

    private String artist;

    public CD() { super(); }

    public CD(String id, String title, String artist) {
        super(id, title);
        this.artist = artist;
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    @Override
    public int getBorrowPeriod() { return 7; }

    @Override
    public double getFinePerDay() { return 20.0; }
}
