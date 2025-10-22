package application;

import domain.Admin;
import domain.AdminStatus;
import java.util.List;
import java.util.logging.Logger;

import persistence.AdminRepository;

/**
 * Provides services and business logic related to administrators in the library system.
 * 
 * <p>This class acts as a bridge between the persistence layer (AdminRepository) and the
 * presentation/application layers. It manages admin registration, removal, authentication,
 * session status (login/logout), and loading admins from external sources.
 * 
 * <p>All operations that involve verifying credentials or updating admin status are
 * encapsulated here to maintain separation of concerns and support testability.
 */
public class AdminService {

  private AdminRepository repository;
  private static final Logger LOGGER = Logger.getLogger(AdminFileLoader.class.getName());

  /**
   * Default constructor.
   * The repository should be set via setter before using service methods.
   */
  public AdminService() {
  }

  /**
   * Constructs an AdminService with a provided repository.
   *
   * @param repository the AdminRepository used for data storage and retrieval
   */
  public AdminService(AdminRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns the repository associated with this service.
   *
   * @return the AdminRepository
   */
  public AdminRepository getRepository() {
    return repository;
  }

  /**
   * Sets the repository for this service.
   *
   * @param repository the AdminRepository to use
   */
  public void setRepository(AdminRepository repository) {
    this.repository = repository;
  }

  /**
   * Registers a new administrator in the system.
   *
   * @param admin the Admin object to register
   */
  public Boolean adminRegister(Admin admin) {
	  if (admin == null) {
	        LOGGER.warning("Cannot register null admin");
	        return null;
	    }
	  
	  Admin existingAdmin  = repository.findAdminByEmail(admin.getName());
	  if(existingAdmin!=null) {
		  LOGGER.warning("Admin with username '" + admin.getName() + "' already exists");
	        return false;
	  }
	  
	  if (admin.getName() == null || admin.getName().isBlank()) {
	        LOGGER.warning("Cannot register admin with empty username");
	        return null;
	    }
	    
	    if (admin.getPassword() == null || admin.getPassword().isBlank()) {
	        LOGGER.warning("Cannot register admin with empty password");
	        return null;
	    }
	  
	  repository.addAdmin(admin);
	    LOGGER.info("Admin '" + admin.getName() + "' registered successfully");
	    return true;
  }

  /**
   * Removes an administrator from the system.
   *
   * @param admin the Admin object to remove
   */
  public Boolean removeAdmin(Admin admin) {
	    if (admin == null) {
	        LOGGER.warning("Cannot remove null admin");
	        return null;
	    }

	    Admin existingAdmin = repository.findAdminByEmail(admin.getName());
	    if (existingAdmin == null) {
	        LOGGER.warning("Cannot remove admin '" + admin.getName() + "' because it does not exist");
	        return false;
	    }

	    repository.removeAdmin(admin);
	    LOGGER.info("Admin '" + admin.getName() + "' removed successfully");
	    return true;
  }

  /**
   * Finds an administrator by their username/email.
   *
   * @param username the username/email to search for
   * @return the Admin object if found, null otherwise
   */
  public Admin findAdminByEmail(String username) {
    return repository.findAdminByEmail(username);
  }

  /**
   * Logs in an administrator by verifying credentials.
   * 
   * <p>Updates the admin's status to ONLINE if successful.
   *
   * @param userName the admin's username
   * @param password the admin's password
   * @return true if login is successful, false otherwise
   */
  public boolean login(String userName, String password) {
	    Admin admin = repository.findAdminByEmail(userName);
	    
	    if (admin == null) {
	        LOGGER.warning("Login failed: user '" + userName + "' not found");
	        return false;
	    }

	    if (!admin.checkPassword(password)) {
	        LOGGER.warning("Login failed: wrong password for user '" + userName + "'");
	        return false;
	    }

	    admin.setStatus(AdminStatus.ONLINE);
	    LOGGER.info("Login successful: user '" + userName + "' is now ONLINE");
	    return true;
	}


  /**
   * Logs out an administrator and updates their status to OFFLINE.
   *
   * @param userName the admin's userName
   */
  public Boolean logout(String userName) {
	    if (userName == null || userName.isBlank()) {
	        LOGGER.warning("Logout failed: username is null or empty");
	        return null;
	    }

	    Admin admin = repository.findAdminByEmail(userName);
	    if (admin == null) {
	        LOGGER.warning("Logout failed: user '" + userName + "' not found");
	        return false;
	    }

	    if (admin.getStatus() != AdminStatus.ONLINE) {
	        LOGGER.info("User '" + userName + "' is already offline");
	        return false;
	    }

	    admin.setStatus(AdminStatus.OFFLINE);
	    LOGGER.info("User '" + userName + "' has successfully logged out");
	    return true;
	}

  /**
   * Loads administrators from an external source and registers them in the repository.
   *
   * @param loader an implementation of AdminSourceLoader to provide admin data
 * @throws Exception 
   */
  public void loadAdmins(AdminSourceLoader loader) throws Exception {
    List<Admin> admins = loader.loadAdmins();
    for (Admin admin : admins) {
      repository.addAdmin(admin);
    }
  }
}
