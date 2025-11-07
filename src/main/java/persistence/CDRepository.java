package persistence;

import domain.CD;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Repository for managing CD entities in temporary in-memory storage.
 * Mirrors the behavior of BookRepository for parity with media types.
 */
public class CDRepository {

    private static final Logger LOGGER = Logger.getLogger(CDRepository.class.getName());
    private static ArrayList<CD> cds = new ArrayList<>();

    /** Add a new CD to the repository. */
    public static void addCD(CD cd) {
        if (cd == null) return;
        cds.add(cd);
        LOGGER.fine(() -> "Added CD: " + cd.getId());
    }

    /** Remove a CD from the repository. */
    public static void removeCD(CD cd) {
        if (cd == null) return;
        cds.remove(cd);
        LOGGER.fine(() -> "Removed CD: " + cd.getId());
    }

    /** Find a CD by its id. */
    public static CD findById(String id) {
        if (id == null) return null;
        return cds.stream().filter(c -> id.equals(c.getId())).findFirst().orElse(null);
    }

    /** Simple keyword search across title, artist, id. Returns first match or null. */
    public static CD searchFirst(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        String lower = keyword.toLowerCase();
        return cds.stream().filter(c ->
                (c.getTitle() != null && c.getTitle().toLowerCase().contains(lower)) ||
                (c.getArtist() != null && c.getArtist().toLowerCase().contains(lower)) ||
                (c.getId() != null && c.getId().toLowerCase().contains(lower))
        ).findFirst().orElse(null);
    }

    /** Return all CDs snapshot. */
    public static List<CD> findAll() { return new ArrayList<>(cds); }

    /** Clear repository (useful for tests). */
    public static void clearCDs() { cds.clear(); }

    /** Find borrowed CDs (active loans perspective). */
    public static List<CD> findAllBorrowed() {
        return cds.stream().filter(CD::isBorrowed).collect(Collectors.toList());
    }

    /** Remove a CD by its id. */
    public static boolean removeById(String id) {
        CD found = findById(id);
        if (found != null) {
            cds.remove(found);
            return true;
        }
        return false;
    }
}