package domain;

/**
 * Represents an administrator user in the library system.
 *
 * <p>The {@code Admin} class extends {@link Person} and adds functionality
 * specific to administrators, such as tracking their operational status
 * using {@link AdminStatus}.</p>
 *
 * <p>Administrators are responsible for managing library operations,
 * overseeing users, and maintaining book and loan data.</p>
 */
public class Admin extends Person {

    /** The current operational status of the administrator. */
    private AdminStatus status;

    /**
     * Default constructor that creates an empty {@code Admin} object.
     * Calls the no-argument constructor of {@link Person}.
     */
    public Admin() {
        super();
    }

    /**
     * Constructs a new {@code Admin} with the specified details.
     *
     * @param id the unique ID of the administrator
     * @param name the name of the administrator
     * @param password the administrator’s login password
     */
    public Admin(String id, String name, String password) {
        super(id, name, password);
    }

    /**
     * Returns the current operational status of the administrator.
     *
     * @return the admin’s {@link AdminStatus} (e.g., ONLINE or OFFLINE)
     */
    public AdminStatus getStatus() {
        return status;
    }

    /**
     * Updates the operational status of the administrator.
     *
     * @param status the new {@link AdminStatus} value
     */
    public void setStatus(AdminStatus status) {
        this.status = status;
    }

    /**
     * Returns a string representation of the administrator,
     * including their ID, name, and status.
     *
     * @return a string describing the admin
     */
    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
