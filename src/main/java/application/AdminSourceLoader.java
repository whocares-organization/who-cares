package application;

import java.util.List;
import domain.*;

/**
 * Represents a source loader for administrators.
 * <p>
 * Implementations of this interface provide a way to load a list of Admin objects
 * from different sources, such as files, databases, or external services.
 * <p>
 * This abstraction allows the AdminService to remain decoupled from the actual
 * data source, supporting flexibility, testability, and future extensions.
 */
public interface AdminSourceLoader {

    /**
     * Loads administrators from a specific source.
     * 
     * @return a list of Admin objects loaded from the source; empty if none found
     */
    List<Admin> loadAdmins();
}
