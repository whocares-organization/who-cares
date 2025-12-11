package application;

import java.util.List;

import domain.Loan;
import domain.Member;
import persistence.MemberRepository;

/**
 * Background watcher dedicated to testing-mode loans.
 * <p>This class owns a daemon thread that periodically checks for loans created via
 * {@link LoanService#borrowMediaTestDuration} and sends real emails using the provided
 * {@link EmailService} when those loans are considered overdue by the application.</p>
 */
public class TestingModeLoanWatcher implements Runnable {

    private final LoanService loanService;
    private final EmailService emailService;
    private final long pollIntervalMillis;
    private Thread thread;

    /**
     * Creates a watcher with a default polling interval of 5 seconds.
     * @param loanService loan service dependency
     * @param emailService email service dependency
     */
    public TestingModeLoanWatcher(LoanService loanService, EmailService emailService) {
        this(loanService, emailService, 5_000L);
    }

    /**
     * Creates a watcher with a custom polling interval (in milliseconds).
     * @param loanService loan service dependency
     * @param emailService email service dependency
     * @param pollIntervalMillis desired poll interval (>0) or defaults to 5000ms
     */
    public TestingModeLoanWatcher(LoanService loanService, EmailService emailService, long pollIntervalMillis) {
        if (loanService == null) {
            throw new IllegalArgumentException("loanService must not be null");
        }
        if (emailService == null) {
            throw new IllegalArgumentException("emailService must not be null");
        }
        this.loanService = loanService;
        this.emailService = emailService;
        this.pollIntervalMillis = pollIntervalMillis <= 0 ? 5_000L : pollIntervalMillis;
    }

    /** Starts the internal daemon thread if it is not already running. */
    public synchronized void start() {
        if (thread != null && thread.isAlive()) {
            return;
        }
        thread = new Thread(this, "TestingModeLoanWatcher");
        thread.setDaemon(true);
        thread.start();
    }

    /** Requests the watcher thread to stop. */
    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * Periodically polls testing-mode loans for expiration and sends notification emails.
     * Removes expired testing-mode loans from tracking.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Loan> overdueTestingLoans = loanService.findOverdueTestingModeLoans();
                for (Loan loan : overdueTestingLoans) {
                    Member member = MemberRepository.findMemberByEmail(loan.getMemberId());
                    if (member == null || member.getUserName() == null || member.getUserName().isBlank()) {
                        continue;
                    }
                    String subject = "[TESTING MODE] Loan expired";
                    String body = "Dear member,\n\n" +
                            "This is an automated notification to inform you that your testing-mode loan for the media item (ISBN: " +
                            loan.getIsbn() + ") has expired.\n" +
                            "Please take the necessary action.\n\n" +
                            "Best regards,\n" +
                            "Library Management System";
                    emailService.sendEmail(member.getUserName(), subject, body);
                    loanService.removeTestingModeLoan(loan);
                }
                Thread.sleep(pollIntervalMillis);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
    LOGGER.severe("[TestingModeLoanWatcher] Error: " + e.getMessage());
}

        }
    }
}
