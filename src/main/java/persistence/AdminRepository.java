package persistence;

import domain.Admin;
import java.util.ArrayList;
import java.util.logging.Logger;

import application.AdminFileLoader;

/**
 * Repository for managing admin-related persistence operations.
 *
 * <p>This class currently acts as a placeholder for admin data access logic.
 * It can be extended to interact with a database or in-memory storage.</p>
 */
public class AdminRepository {

  private static ArrayList<Admin> admins = new ArrayList<>();
  private static final Logger LOGGER = Logger.getLogger(AdminFileLoader.class.getName());

  /**
   * Creates a new {@code AdminRepository} instance with default configuration.
   */
  public AdminRepository() {
      // default constructor
  }

  /**
   * Adds a new admin to the repository.
   *
   * @param admin the Admin object to add
   */
  public static void addAdmin(Admin admin) {
    admins.add(admin);
  }

  /**
   * Removes an existing admin from the repository.
   *
   * @param admin the Admin object to remove
   */
  public static void removeAdmin(Admin admin) {
    admins.remove(admin);
  }

  /**
   * Searches for an admin by username.
   *
   * @param userName the username to search for
   * @return the Admin object if found, or {@code null} if no matching admin exists
   */
  public static Admin findAdminByEmail(String userName) {
	    return admins.stream()
	        .filter(admin -> admin.getUserName().equals(userName))
	        .findFirst()
	        .map(admin -> {
	            LOGGER.info("Admin '" + userName + "' found");
	            return admin;
	        })
	        .orElseGet(() -> {
	            LOGGER.warning("Admin '" + userName + "' not found");
	            return null;
	        });
	}

  /**
   * Clears all admins from the repository (useful for tests).
   */
  public static void clearAdmins() {
	    admins.clear();
	}
}