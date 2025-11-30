package application;

import domain.Admin;

import java.util.List;

/**
 * Represents a source loader for administrators.
 *
 * <p>Implementations of this interface provide a way to load a list of Admin objects
 * from different sources, such as files, databases, or external services.</p>
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AdminSourceLoader {

  /**
   * Loads administrators from a specific source.
   *
   * @return a list of Admin objects loaded from the source; empty if none found
   * @throws Exception if loading fails due to I/O, parsing, or access issues
   */
  List<Admin> loadAdmins() throws Exception;
}