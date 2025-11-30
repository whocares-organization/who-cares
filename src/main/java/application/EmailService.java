package application;

/**
 * Abstraction for sending emails. Implementations may send real emails or log to console.
 */
public interface EmailService {

    /**
     * Sends an email message.
     * @param to recipient identifier (email/username)
     * @param subject subject line (may be null for none)
     * @param body body content (may be empty)
     */
    void sendEmail(String to, String subject, String body);
}