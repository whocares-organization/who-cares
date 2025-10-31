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

    /** The unique identifier of the person. */
    protected String id;

    /** The full name of the person. */
    protected String name;

    /** The password used for authentication. */
    protected String password;

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
    public Person(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Returns the unique identifier of the person.
     *
     * @return the person's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the person.
     *
     * @param id the person's ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the name of the person.
     *
     * @return the person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the person.
     *
     * @param name the person's name
     */
    public void setName(String name) {
        this.name = name;
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
