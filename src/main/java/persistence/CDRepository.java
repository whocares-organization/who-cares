package persistence;

import domain.CD;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * In-memory repository for {@link CD} media items.
 *
 * <p>Supports storing, retrieving, and status updates for CDs.
 * Not intended for production use.</p>
 */
public class CDRepository {

    private static final Logger LOGGER = Logger.getLogger(CDRepository.class.getName());
    private static ArrayList<CD> cds = new ArrayList<>();

    /**
     * Creates a new {@code CDRepository} with empty storage.
     */
    public CDRepository() { }

    /**
     * Add a new CD to the repository.
     * @param cd the CD instance to add
     */
    public static void addCD(CD cd) {
        if (cd == null) return;
        cds.add(cd);
        LOGGER.fine(() -> "Added CD: " + cd.getId());
    }

    /**
     * Remove a CD from the repository.
     * @param cd the CD instance to remove
     */
    public static void removeCD(CD cd) {
        if (cd == null) return;
        cds.remove(cd);
        LOGGER.fine(() -> "Removed CD: " + cd.getId());
    }

    /**
     * Find a CD by its id.
     * @param id the CD identifier
     * @return the matching CD or {@code null} if not found
     */
    public static CD findById(String id) {
        if (id == null) return null;
        return cds.stream().filter(c -> id.equals(c.getId())).findFirst().orElse(null);
    }

    /**
     * Simple keyword search across title, artist, id.
     * Returns the first match or {@code null}.
     * @param keyword the search keyword
     * @return a matching CD or {@code null}
     */
    public static CD searchFirst(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        String lower = keyword.toLowerCase();
        return cds.stream().filter(c ->
                (c.getTitle() != null && c.getTitle().toLowerCase().contains(lower)) ||
                (c.getArtist() != null && c.getArtist().toLowerCase().contains(lower)) ||
                (c.getId() != null && c.getId().toLowerCase().contains(lower))
        ).findFirst().orElse(null);
    }

    /**
     * Return a snapshot of all CDs.
     * @return a new list containing all CDs
     */
    public static List<CD> findAll() { return new ArrayList<>(cds); }

    /**
     * Clear repository (useful for tests).
     */
    public static void clearCDs() { cds.clear(); }

    /**
     * Find borrowed CDs (active loans perspective).
     * @return list of CDs currently marked as borrowed
     */
    public static List<CD> findAllBorrowed() {
        return cds.stream().filter(CD::isBorrowed).collect(Collectors.toList());
    }

    /**
     * Remove a CD by its id.
     * @param id the CD identifier
     * @return {@code true} if a CD was removed; {@code false} otherwise
     */
    public static boolean removeById(String id) {
        CD found = findById(id);
        if (found != null) {
            cds.remove(found);
            return true;
        }
        return false;
    }
}
