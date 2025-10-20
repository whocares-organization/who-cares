package persistence;

import domain.Admin;
import java.util.ArrayList;
import java.util.logging.Logger;

import application.AdminFileLoader;

/**
 * Repository class for managing Admin entities in memory.
 * 
 * <p>Provides methods to add, remove, and search for administrators by username.
 * This is a simple in-memory storage implementation using a static ArrayList.
 */
public class AdminRepository {

  private static ArrayList<Admin> admins = new ArrayList<>();
  private static final Logger LOGGER = Logger.getLogger(AdminFileLoader.class.getName());

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
   * @return the Admin object if found, or null if no matching admin exists
   */
  public static Admin findAdminByEmail(String userName) {
	    return admins.stream()
	        .filter(admin -> admin.getUsername().equals(userName))
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

  public static void clearAdmins() {
	    admins.clear();
	}
  
}
