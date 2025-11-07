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

    /**
     * Default constructor that creates an empty {@code Person} object.
     */
    public Person() {}

    /**
     * Constructs a new {@code Person} with the specified details.
     *
     * @param id the unique identifier for the person
     * @param name the name of the person
     * @param password the password associated with the person
     */
    public Person(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    
    public Person(String id,String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.id = id;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	/**
     * Constructs a new User with basic information.
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
     * Returns the name of the person.
     *
     * @return the person's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the person.
     *
     * @param name the person's name
     */
    public void setUserName(String name) {
        this.userName = userName;
    }

    /**
     * Returns the password of the person.
     *
     * @return the person's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the password of the person.
     *
     * @param password the new password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

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
