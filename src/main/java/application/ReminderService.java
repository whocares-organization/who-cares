package application;

import java.time.LocalDate;
import java.util.List;

import domain.Loan;
import domain.Member;

/**
 * Coordinates periodic reminder operations, such as scanning for overdue loans
 * and notifying members via email.
 */
public class ReminderService {

    private final LoanService loanService;
    private final MemberService memberService;
    private final EmailService emailService;

    /**
     * Creates a {@code ReminderService} with default dependencies.
     */
    public ReminderService() {
		this.loanService = new LoanService();
		this.memberService = new MemberService();
		this.emailService = null;
	}

    /**
     * Creates a {@code ReminderService} with explicit service dependencies.
     *
     * @param loanService service used to query loans
     * @param memberService service used to query members
     * @param emailService service used to send notifications
     */
    public ReminderService(LoanService loanService, MemberService memberService, EmailService emailService) {
        this.loanService = loanService;
        this.memberService = memberService;
        this.emailService = emailService;
    }
    

    /**
     * Sends reminder emails to all members who currently have overdue loans.
     */
    public void sendOverdueReminders() {
        LocalDate today = LocalDate.now();
        for (Member member : memberService.getAllMembers()) {
            sendReminderToMember(member, today);
        }
    }

    /**
     * Sends a reminder email to a specific member if they have overdue loans.
     * @param member target member (nullable)
     * @return true if processed, false or null otherwise (null for invalid member)
     */
    public Boolean sendReminderToSpecificMember(Member member) {
        if (member == null) return null;
        if (member.getUserName() == null || member.getUserName().isBlank()) return null;

        LocalDate today = LocalDate.now();
        sendReminderToMember(member, today);
        return true;
    }

    /**
     * Internal helper that checks for overdue loans of a given member and sends a reminder email.
     * @param member member to inspect (must not be null)
     * @param date reference date for overdue evaluation
     */
    private void sendReminderToMember(Member member, LocalDate date) {
        List<Loan> overdueLoans = loanService.getOverdueLoansForMember(member.getUserName(), date);
        if (!overdueLoans.isEmpty()) {
            String subject = "Library Overdue Reminder"; // added subject
            String body = "You have " + overdueLoans.size() + " overdue book(s).";
            emailService.sendEmail(member.getUserName(), subject, body); // fixed to match interface
        }
    }

}