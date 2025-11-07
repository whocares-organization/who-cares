package applicationtest;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.EmailService;
import application.LoanService;
import application.MemberService;
import application.ReminderService;
import domain.Loan;
import domain.Member;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock private LoanService loanService;
    @Mock private MemberService memberService;
    @Mock private EmailService emailService;

    @InjectMocks private ReminderService reminderService;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
    }

    @Test
    void sendsReminderWhenOverdue() {
        Member member = new Member("user@example.com", "pw");
        Loan overdue = new Loan("B1", "user@example.com", today.minusDays(10), today.minusDays(1));

        when(memberService.getAllMembers()).thenReturn(List.of(member));
        when(loanService.getOverdueLoansForMember("user@example.com", today))
            .thenReturn(List.of(overdue));

        reminderService.sendOverdueReminders();

        verify(emailService).sendEmail("user@example.com", "You have 1 overdue book(s).");
    }

    @Test
    void doesNotSendReminderWhenNoOverdue() {
        Member member = new Member("user@example.com", "pw");

        when(memberService.getAllMembers()).thenReturn(List.of(member));
        when(loanService.getOverdueLoansForMember("user@example.com", today))
            .thenReturn(List.of()); // لا قروض متأخرة

        reminderService.sendOverdueReminders();

        verify(emailService, never()).sendEmail("user@example.com", "You have 0 overdue book(s).");
    }

    @Test
    void sendsCorrectMessageWhenMultipleOverdues() {
        Member member = new Member("alice@example.com", "pw");
        Loan overdue1 = new Loan("B1", "alice@example.com", today.minusDays(10), today.minusDays(1));
        Loan overdue2 = new Loan("B2", "alice@example.com", today.minusDays(8), today.minusDays(2));

        when(memberService.getAllMembers()).thenReturn(List.of(member));
        when(loanService.getOverdueLoansForMember("alice@example.com", today))
            .thenReturn(List.of(overdue1, overdue2));

        reminderService.sendOverdueReminders();

        verify(emailService).sendEmail("alice@example.com", "You have 2 overdue book(s).");
    }

    @Test
    void sendReminderToSpecificMember_returnsNullForNullMember() {
        Member member = null;

        Boolean result = reminderService.sendReminderToSpecificMember(member);

        assertNull(result);

        verify(emailService, never()).sendEmail(any(), any());
    }

    
    @Test
    void sendReminderToSpecificMember_returnsNullForMemberWithEmptyUsername() {
        Member member = new Member("", "pw");

        Boolean result = reminderService.sendReminderToSpecificMember(member);

        assertNull(result);
        verify(emailService, never()).sendEmail(any(), any());
    }
    
    @Test
    void sendReminderToSpecificMember_returnsNullForMemberWithNullUsername() {
        Member member = new Member(null, "pw");

        Boolean result = reminderService.sendReminderToSpecificMember(member);

        assertNull(result);
        verify(emailService, never()).sendEmail(any(), any());
    }

    
    @Test
    void doesNotSendReminderForMemberWithEmptyUsername() {
        Member member = new Member("", "pw");

        when(memberService.getAllMembers()).thenReturn(List.of(member));

        reminderService.sendOverdueReminders();

        verify(emailService, never()).sendEmail("", "You have 1 overdue book(s).");
    }

    @Test
    void sendReminderToSpecificMember_onlySendsIfOverdue() {
        Member member = new Member("bob@example.com", "pw");
        Loan overdue = new Loan("B1", "bob@example.com", today.minusDays(5), today.minusDays(1));

        when(loanService.getOverdueLoansForMember("bob@example.com", today))
            .thenReturn(List.of(overdue));

        reminderService.sendReminderToSpecificMember(member);

        verify(emailService).sendEmail("bob@example.com", "You have 1 overdue book(s).");
    }

    @Test
    void sendReminderToSpecificMember_doesNotSendIfNoOverdue() {
        Member member = new Member("bob@example.com", "pw");

        when(loanService.getOverdueLoansForMember("bob@example.com", today))
            .thenReturn(List.of());

        reminderService.sendReminderToSpecificMember(member);

        verify(emailService, never()).sendEmail("bob@example.com", "You have 0 overdue book(s).");
    }

    @Test
    void sendReminderToSpecificMember_doesNotSendForNullMember() {
        reminderService.sendReminderToSpecificMember(null);

        verify(emailService, never()).sendEmail(null, "You have 1 overdue book(s).");
    }

}
