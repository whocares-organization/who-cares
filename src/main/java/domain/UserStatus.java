package domain;

/**
 * Represents the operational status of an administrator in the library system.
 * Tracks whether an administrator is currently active and available for administrative tasks.
 */
public enum UserStatus {

    /**
     * Administrator is currently logged in and active.
     * Indicates that the admin can perform administrative tasks.
     */
    ONLINE,

    /**
     * Administrator is not currently logged in or active.
     * Indicates that the admin is unavailable for administrative tasks.
     */
    OFFLINE
}
