package applicationtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.EmailService;
import application.LoanService;
import application.MemberService;
import application.ReminderService;
import domain.Book;
import domain.Loan;
import domain.Member;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

class ReminderServiceTest {

    private LoanService loanService;
    private MemberService memberService;
    private EmailService emailService;
    private ReminderService reminderService;

    @BeforeEach
    void setup() {
        BookRepository.clearBooks();
        MemberRepository.clearMembers();
        LoanRepository.clearLoans();

        loanService = new LoanService();
        memberService = new MemberService(new MemberRepository());
        emailService = mock(EmailService.class);
        reminderService = new ReminderService(loanService, memberService, emailService);

        Member m = new Member("member@example.com", "pw");
        memberService.registerMember(m);
        Book b = new Book("Title", "Auth", "ISBN1");
        BookRepository.addBook(b);
        // create overdue loan manually
        Loan overdue = new Loan("ISBN1", m.getUserName(), LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        LoanRepository.save(overdue);
    }

    @Test
    void sendOverdueReminders_sendsEmailForMemberWithOverdueLoans() {
        reminderService.sendOverdueReminders();
        verify(emailService, times(1)).sendEmail(eq("member@example.com"), anyString(), contains("overdue"));
    }

    @Test
    void sendReminderToSpecificMember_returnsTrueAndSendsEmail() {
        Member m = memberService.findMemberByEmail("member@example.com");
        assertTrue(reminderService.sendReminderToSpecificMember(m));
        verify(emailService, times(1)).sendEmail(eq("member@example.com"), anyString(), anyString());
    }

    @Test
    void sendReminderToSpecificMember_nullMemberReturnsNull() {
        assertNull(reminderService.sendReminderToSpecificMember(null));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void sendReminderToSpecificMember_UserNameNull_ReturnsNull() {
        Member invalid = new Member("x@example.com", "pw");
        invalid.setUserName(null);
        memberService.registerMember(invalid);

        assertNull(reminderService.sendReminderToSpecificMember(invalid));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendReminderToSpecificMember_BlankUserName_ReturnsNull() {
        Member invalid = new Member("blank@example.com", "pw");
        invalid.setUserName("   ");
        memberService.registerMember(invalid);

        assertNull(reminderService.sendReminderToSpecificMember(invalid));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

   

    
}
