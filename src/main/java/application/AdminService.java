package application;

import domain.Admin;
import domain.UserStatus;
import java.util.List;
import java.util.logging.Logger;

import persistence.AdminRepository;

/**
 * Provides services and business logic related to administrators in the library system.
 *
 * <p>This class acts as a bridge between the persistence layer (AdminRepository) and the
 * presentation/application layers. It manages admin registration, removal, authentication,
 * session status (login/logout), and loading admins from external sources.</p>
 */
public class AdminService {

    private AdminRepository repository;
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    /**
     * Default constructor.
     */
    public AdminService() {
    }

    /**
     * Constructs an AdminService with a provided repository.
     *
     * @param repository the repository instance to use
     */
    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns the repository associated with this service.
     *
     * @return the admin repository
     */
    public AdminRepository getRepository() {
        return repository;
    }

    /**
     * Sets the repository for this service.
     *
     * @param repository the repository to set
     */
    public void setRepository(AdminRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers a new administrator in the system.
     *
     * @param admin the Admin object to register
     * @return true if registration succeeded, false if user exists, null if invalid input
     */
    public Boolean adminRegister(Admin admin) {
        if (admin == null) {
            LOGGER.warning("Cannot register null admin");
            return null;
        }

        Admin existingAdmin = repository.findAdminByEmail(admin.getUserName());
        if (existingAdmin != null) {
            LOGGER.warning("Admin with username '" + admin.getUserName() + "' already exists");
            return false;
        }

        if (admin.getUserName() == null || admin.getUserName().isBlank()) {
            LOGGER.warning("Cannot register admin with empty username");
            return null;
        }

        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            LOGGER.warning("Cannot register admin with empty password");
            return null;
        }

        repository.addAdmin(admin);
        LOGGER.info("Admin '" + admin.getUserName() + "' registered successfully");
        return true;
    }

    /**
     * Removes an administrator from the system.
     *
     * @param admin the Admin object to remove
     * @return true if removed successfully, false if admin not found, null if input is null
     */
    public Boolean removeAdmin(Admin admin) {
        if (admin == null) {
            LOGGER.warning("Cannot remove null admin");
            return null;
        }

        Admin existingAdmin = repository.findAdminByEmail(admin.getUserName());
        if (existingAdmin == null) {
            LOGGER.warning("Cannot remove admin '" + admin.getUserName() + "' because it does not exist");
            return false;
        }

        repository.removeAdmin(admin);
        LOGGER.info("Admin '" + admin.getUserName() + "' removed successfully");
        return true;
    }

    /**
     * Finds an administrator by their username/email.
     *
     * @param username the admin's username/email
     * @return the found admin or {@code null} if not found
     */
    public Admin findAdminByEmail(String username) {
        return repository.findAdminByEmail(username);
    }

    /**
     * Logs in an administrator by verifying credentials.
     * Updates status to ONLINE if successful.
     *
     * @param userName the admin's username/email
     * @param password the plain password to check
     * @return {@code true} if login succeeds; {@code false} otherwise
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

        admin.setStatus(UserStatus.ONLINE);
        LOGGER.info("Login successful: user '" + userName + "' is now ONLINE");
        return true;
    }

    /**
     * Logs out an administrator and updates their status to OFFLINE.
     *
     * @param userName the admin's username
     * @return true if logout succeeded, false if user not found or already offline, null if input invalid
     */
    public Boolean logout(String userName) {
       
		if (userName == null || userName.isBlank()) {
			LOGGER.warning("Cannot logout with null or empty username");
			return null;
		}
		
        Admin admin = repository.findAdminByEmail(userName);
        if (admin == null) {
            LOGGER.warning("Logout failed: user '" + userName + "' not found");
            return false;
        }

        if (admin.getStatus() != UserStatus.ONLINE) {
            LOGGER.info("User '" + userName + "' is already offline");
            return false;
        }

        admin.setStatus(UserStatus.OFFLINE);
        LOGGER.info("User '" + userName + "' has successfully logged out");
        return true;
    }

    /**
     * Loads administrators from an external source and registers them in the repository.
     *
     * @param loader an implementation of AdminSourceLoader to provide admin data
     * @throws Exception if the loader fails to fetch or parse data
     */
    public void loadAdmins(AdminSourceLoader loader) throws Exception {
        List<Admin> admins = loader.loadAdmins();
        for (Admin admin : admins) {
            repository.addAdmin(admin);
        }
    }
}