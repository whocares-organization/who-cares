package domain;

/**
 * Represents an administrator user in the library system.
 *
 * <p>The {@code Admin} class extends {@link Person} and adds functionality
 * specific to administrators, such as tracking their operational status
 * using {@link UserStatus}.</p>
 */
public class Admin extends Person {

    /**
     * Default constructor that creates an empty {@code Admin} object.
     */
    public Admin() {
        super();
    }

    /**
     * Constructs a new {@code Admin} with the specified credentials.
     *
     * @param userName the administrator's username/email
     * @param password the administrator's login password
     */
    public Admin(String userName, String password) {
        super(userName, password);
    }
    
    /**
     * Constructs a new {@code Admin} with full profile information.
     *
     * @param userName the administrator's username/email
     * @param password the administrator's password
     * @param name the administrator's full name
     * @param id the unique administrator identifier
     * @param phone the administrator's phone number
     */
    public Admin(String userName, String password, String name, String id, String phone) {
       super(userName, password, name, id, phone);
    }

    /**
     * Returns a string representation of the administrator,
     * including their name and status.
     *
     * @return a string describing the admin
     */
    @Override
    public String toString() {
        return "Admin{" +
                ", name='" + this.getName() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}