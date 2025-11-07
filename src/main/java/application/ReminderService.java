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
     */
    public void sendOverdueReminders() {
        LocalDate today = LocalDate.now();
        for (Member member : memberService.getAllMembers()) {
            sendReminderToMember(member, today);
        }
    }

    /**
     * Sends a reminder email to a specific member if they have overdue loans.
     */
    public Boolean sendReminderToSpecificMember(Member member) {
        if (member == null) return null;
        if (member.getUserName() == null || member.getUserName().isBlank()) return null;

        LocalDate today = LocalDate.now();
        sendReminderToMember(member, today);
        return true;
    }

    /**
     * Internal helper method that checks for overdue loans of a given member and sends a reminder email.
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
