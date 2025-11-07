package application;

import domain.Member;
import domain.Loan;

import java.time.LocalDate;
import java.util.List;

public class ReminderService {

    private final LoanService loanService;
    private final MemberService memberService;
    private final EmailService emailService;

    public ReminderService(LoanService loanService, MemberService memberService, EmailService emailService) {
        this.loanService = loanService;
        this.memberService = memberService;
        this.emailService = emailService;
    }

    /**
     * Sends reminder emails to all members who currently have overdue loans.
     * <p>
     * This method retrieves all registered members from {@link MemberService},
     * checks their loan status using {@link LoanService}, and sends an email
     * notification via {@link EmailService} if any overdue loans are found.
     * </p>
     */
    public void sendOverdueReminders() {
        LocalDate today = LocalDate.now();
        for (Member member : memberService.getAllMembers()) {
            sendReminderToMember(member, today);
        }
    }

    /**
     * Sends a reminder email to a specific member if they have overdue loans.
     * <p>
     * If the given member has no overdue books or the member reference is null,
     * no email is sent. This is a more targeted alternative to
     * {@link #sendOverdueReminders()}.
     * </p>
     *
     * @param member the {@link Member} to check for overdue loans
     */
    public Boolean sendReminderToSpecificMember(Member member) {
        if (member == null) return null;
        if(member.getUserName() == null || member.getUserName().isBlank()) return null;
        LocalDate today = LocalDate.now();
        sendReminderToMember(member, today);
        return true;
    }

    /**
     * Internal helper method that checks for overdue loans of a given member and,
     * if found, sends a reminder email.
     * <p>
     * This method is used internally by {@link #sendOverdueReminders()} and
     * {@link #sendReminderToSpecificMember(Member)}. It validates the member's
     * username and only sends emails if there are overdue loans.
     * </p>
     *
     * @param member the {@link Member} to check for overdue loans
     * @param date   the current date used to determine which loans are overdue
     */
    private void sendReminderToMember(Member member, LocalDate date) {
        List<Loan> overdueLoans = loanService.getOverdueLoansForMember(member.getUserName(), date);
        if (!overdueLoans.isEmpty()) {
            String message = "You have " + overdueLoans.size() + " overdue book(s).";
            emailService.sendEmail(member.getUserName(), message);
        }
    }

}
