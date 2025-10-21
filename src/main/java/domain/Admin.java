package domain;

/**
 * Represents an administrator in the library system.
 * 
 * <p>This class stores the administrator's credentials (username and password) and tracks
 * their operational status (ONLINE/OFFLINE) for session management.
 */
public class Admin {

  private String username;
  private String password;
  private AdminStatus status;

  /**
   * Constructs a new Admin with no initialized credentials or status.
   */
  public Admin() {
  }

  /**
   * Constructs a new Admin with the specified username and password.
   *
   * <p>The initial status is not set and should be managed separately.
   *
   * @param userName the administrator's unique username
   *
   * @param password the administrator's authentication password
   */
  public Admin(String userName, String password) {
    this.username = userName;
    this.password = password;
  }

  /**
   * Returns the administrator's username.
   *
   * @return the current username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets or updates the administrator's username.
   *
   * @param userName the new username to set
   */
  public void setUserName(String userName) {
    this.username = userName;
  }

  /**
   * Returns the administrator's password.
   *
   * @return the current password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets or updates the administrator's password.
   *
   * @param password the new password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Returns the current operational status of the administrator.
   *
   * @return the current AdminStatus (ONLINE or OFFLINE)
   */
  public AdminStatus getStatus() {
    return status;
  }

  /**
   * Updates the operational status of the administrator.
   *
   * @param status the new AdminStatus to set
   */
  public void setStatus(AdminStatus status) {
    this.status = status;
  }

  /**
   * Checks if the provided password matches the administrator's current password.
   *
   * @param password the password to verify
   *
   * @return true if the provided password matches the current password, false otherwise
   */
  public boolean checkPassword(String password) {
      return this.password != null && this.password.equals(password);
  }


}
