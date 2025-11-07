package application;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

public class SendRealEmail implements EmailService {
    private final Session session;
    private final String from;
    private final Logger logger = Logger.getLogger(SendRealEmail.class.getName());

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

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            if (to == null || to.isBlank()) {
                throw new IllegalArgumentException("Recipient email address (to) cannot be null or empty.");
            }

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject != null ? subject : "(no subject)");
            msg.setText(body != null ? body : "");
            Transport.send(msg);
            logger.info("✅ Email sent successfully to " + to);
        } catch (MessagingException | IllegalArgumentException e) {
            logger.log(Level.SEVERE, "❌ Error sending email to " + to, e);
            throw new RuntimeException(e);
        }
    }

   
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

    public static void main(String[] args) {
        run();
    }
}
