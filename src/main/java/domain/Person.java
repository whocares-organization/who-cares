package domain;

/**
 * An abstract base class representing a person in the library system.
 *
 * <p>The {@code Person} class serves as a common parent for all types of users
 * in the system, such as {@link Admin} and {@link Member}. It contains shared
 * attributes like ID, name, and password, along with common operations such as
 * password verification.</p>
 */
public abstract class Person {

    /** The username or email used for authentication. */
    private String userName;

    /** The password used for authentication. */
    private String password;
    
    /** The full name of the user. */
    private String name;
    
    /** The unique identifier for the user (e.g., national ID or system ID). */
    private String id;
    
    /** The user's phone number. */
    private String phone;
    
    /** The date when the user account was created. */
    private String createdAt;
    
    private UserStatus status;

    /**
     * Returns the current status of the user.
     * @return the status value
     */
    public UserStatus getStatus() { return status; }

    /**
     * Updates the current status of the user.
     * @param status the new status to set
     */
    public void setStatus(UserStatus status) { this.status = status; }

    /**
     * Default no-argument constructor.
     * Initializes an empty {@code Person} instance.
     */
    public Person() {}

    /**
     * Constructs a new {@code Person} with the specified username and password.
     *
     * @param userName the username or email used for login
     * @param password the user's password
     */
    public Person(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * Constructs a new {@code Person} with an explicit ID, username, and password.
     *
     * @param id the unique identifier for the person
     * @param userName the username or email used for login
     * @param password the password associated with the person
     */
    public Person(String id, String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.id = id;
    }
    
    /**
     * Returns the full name of the user.
     * @return the name value
     */
    public String getName() { return name; }

    /**
     * Updates the full name of the user.
     * @param name the new name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the unique identifier of the user.
     * @return the ID value
     */
    public String getId() { return id; }

    /**
     * Updates the unique identifier of the user.
     * @param id the new identifier to set
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the user's phone number.
     * @return the phone value
     */
    public String getPhone() { return phone; }

    /**
     * Updates the user's phone number.
     * @param phone the new phone value to set
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Returns the account creation date.
     * @return the creation date string
     */
    public String getCreatedAt() { return createdAt; }

    /**
     * Updates the account creation date.
     * @param createdAt the creation date string to set
     */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Constructs a new user with basic information.
     *
     * @param userName the username or email used for login
     * @param password the user's password
     * @param name the full name of the user
     * @param id the unique identifier
     * @param phone the user's phone number
     */
    public Person(String userName, String password, String name, String id, String phone) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.createdAt = java.time.LocalDate.now().toString();
    }

    /**
     * Returns the username or email used for login.
     * @return the username value
     */
    public String getUserName() { return userName; }

    /**
     * Updates the username or email used for login.
     * @param userName the new username to set
     */
    public void setUserName(String userName) { this.userName = userName; }

    /**
     * Returns the password associated with the user.
     * @return the password value
     */
    public String getPassword() { return password; }

    /**
     * Updates the password of the user.
     * @param password the new password to set
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Verifies whether the provided password matches the stored one.
     *
     * @param password the password to verify
     * @return {@code true} if the provided password matches the current password,
     *         {@code false} otherwise
     */
    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }
}