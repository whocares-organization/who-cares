package application;

import domain.Loan;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Observer that listens for overdue loan notifications and triggers an email.
 * Sends email notifications when overdue loans are detected.
 */
public class OverdueEmailObserver implements Observer {

    private static final Logger LOGGER = Logger.getLogger(OverdueEmailObserver.class.getName());
    private final EmailService emailService;

    /**
     * Creates a new observer using the provided email service.
     *
     * @param emailService the email service used to send notifications
     */
    public OverdueEmailObserver(EmailService emailService) {
        if (emailService == null) throw new IllegalArgumentException("EmailService cannot be null");
        this.emailService = emailService;
    }

    /**
     * Receives an overdue loan event and sends an email notification.
     *
     * @param o   observable source (expected LoanService)
     * @param arg payload (expected a Loan instance)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof Loan)) {
            LOGGER.warning("Observer invoked with unexpected payload: " + arg);
            return;
        }
        Loan loan = (Loan) arg;
        String to = loan.getMemberId(); // username is treated as email
        String subject = "Library: Book Overdue";
        String body = "Overdue notice for " + loan.getMemberId() + ": Your loan for ISBN "
                + loan.getIsbn() + " is overdue. Current fine: " + loan.getFineAmount();
        emailService.sendEmail(to, subject, body);
    }
}