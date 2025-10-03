package application;

import java.util.List;
import domain.*;
import persistence.*;

/**
 * Provides services and business logic related to administrators in the library system.
 * <p>
 * This class acts as a bridge between the persistence layer (AdminRepository) and the
 * presentation/application layers. It manages admin registration, removal, authentication,
 * session status (login/logout), and loading admins from external sources.
 * <p>
 * All operations that involve verifying credentials or updating admin status are
 * encapsulated here to maintain separation of concerns and support testability.
 */
public class AdminService {

    private AdminRepository repository;

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
    public void adminRegister(Admin admin) {
        repository.addAdmin(admin);
    }

    /**
     * Removes an administrator from the system.
     * 
     * @param admin the Admin object to remove
     */
    public void removeAdmin(Admin admin) {
        repository.removeAdmin(admin);
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
     * Updates the admin's status to ONLINE if successful.
     * 
     * @param userName the admin's username
     * @param password the admin's password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String userName, String password) {
        Admin admin = repository.findAdminByEmail(userName);
        if(admin != null && admin.checkPassword(password)) {
            admin.setStatus(AdminStatus.ONLINE);
            return true;
        }
        return false;
    }

    /**
     * Logs out an administrator and updates their status to OFFLINE.
     * 
     * @param username the admin's username
     */
    public void logout(String username) {
        Admin admin = repository.findAdminByEmail(username);
        if (admin != null) {
            admin.setStatus(AdminStatus.OFFLINE);
        }
    }

    /**
     * Loads administrators from an external source and registers them in the repository.
     * 
     * @param loader an implementation of AdminSourceLoader to provide admin data
     */
    public void loadAdmins(AdminSourceLoader loader) {
        List<Admin> admins = loader.loadAdmins();
        for (Admin admin : admins) {
            repository.addAdmin(admin);
        }
    }
}
