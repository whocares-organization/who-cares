package application;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Utility implementation of {@link EmailService} that sends real emails via SMTP.
 *
 * <p>Credentials can be supplied via constructor parameters or environment variables
 * (e.g., using dotenv), depending on the runtime configuration.</p>
 */
public class SendRealEmail implements EmailService {
    private final Session session;
    private final String from;
    private final Logger logger = Logger.getLogger(SendRealEmail.class.getName());

    /**
     * Creates an email sender with the given credentials.
     *
     * @param username the SMTP account username
     * @param password the SMTP account password
     */
    public SendRealEmail(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Email username or password cannot be null.");
        }

        this.from = username;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Sends an email with the specified subject and body to the given recipient.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param body    the body of the email
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        // Validate inputs and propagate IllegalArgumentException directly as the tests expect.
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email address (to) cannot be null or empty.");
        }

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject != null ? subject : "(no subject)");
            msg.setText(body != null ? body : "");
            Transport.send(msg);
            logger.info("✅ Email sent successfully to " + to);
        } catch (MessagingException e) {
            // Wrap MessagingException as RuntimeException, preserving the cause for testing.
            logger.log(Level.SEVERE, "❌ Error sending email to " + to, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a demo routine for sending a test email.
     */
    public static void run() {
        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");

        if (username == null || password == null) {
            System.err.println("❌ Missing EMAIL_USERNAME or EMAIL_PASSWORD in .env file!");
            return;
        }

        EmailService emailService = new SendRealEmail(username, password);

        String subject = "Book Due Reminder";
        String body = "Dear user, Your book is due soon. Best regards, An Najah Library System";

        
        String recipient = "s12218103@stu.najah.edu";
        emailService.sendEmail(recipient, subject, body);
    }

    /**
     * CLI entry point for sending a test email.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        //run();
    }
}