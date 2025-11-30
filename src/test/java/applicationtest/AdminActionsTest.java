package applicationtest;

import application.AdminActions;
import application.AdminFileLoader;
import application.BorrowingRules;
import application.EmailService;
import application.LoanService;
import application.MemberService;
import domain.Admin;
import domain.Loan;
import domain.UserStatus;
import domain.Member;
import persistence.AdminRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AdminActionsTest {

    private AdminActions adminActions;
    private Admin existingAdmin;

    @BeforeEach
    void setUp() throws Exception {
        adminActions = new AdminActions();
        existingAdmin = new AdminFileLoader("admins.txt").loadAdmins().get(0);
        AdminRepository.addAdmin(existingAdmin);
        // Ensure admin is logged in for privileged operations
        existingAdmin.setStatus(UserStatus.ONLINE);
    }

    @AfterEach
    void tearDown() {
        AdminRepository.clearAdmins();
        MemberRepository.clearMembers();
        LoanRepository.clearLoans();
        adminActions = null;
        existingAdmin = null;
    }

    // ================= isAdmin Tests =================
    @Test
    void isAdminWithExistingAdmin_ShouldReturnTrue() {
        boolean result = adminActions.isAdmin(existingAdmin);
        assertTrue(result, "Existing admin should be recognized as admin");
    }

    @Test
    void isAdminWithNonExistingAdmin_ShouldReturnFalse() {
        Admin notInRepo = new Admin("NonExisting", "pw");
        boolean result = adminActions.isAdmin(notInRepo);
        assertFalse(result, "Non-existing admin should not be recognized as admin");
    }
    // ==================================================

    // ================= Register Member Tests =================
    @Test
    void registerMemberWithValidAdmin_ShouldSucceed() {
        Member member = new Member("user@example.com", "pw");
        adminActions.registerMember(existingAdmin, member);
        assertNotNull(MemberRepository.findMemberByEmail("user@example.com"), "Member should be registered");
    }

    @Test
    void registerMemberWithNonAdmin_ShouldThrow() {
        Admin notAdmin = new Admin("Ghost", "pw");
        Member member = new Member("user2@example.com", "pw");
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminActions.registerMember(notAdmin, member));
        assertEquals("Admin must be logged in to register members.", ex.getMessage());
        assertNull(MemberRepository.findMemberByEmail("user2@example.com"), "Member should not be registered");
    }
    // ==========================================================

    // ================= canBeUnregistered Tests =================
    @Test
    void canBeUnregisteredWithNoLoansAndNoFines_ShouldReturnTrue() {
        Member member = new Member("M-1", "user1@example.com", "pw");
        boolean result = adminActions.canBeUnregistered(member);
        assertTrue(result, "Member with no loans/fines should be unregisterable");
    }

    @Test
    void canBeUnregisteredWithActiveLoans_ShouldReturnFalse() {
        Member member = new Member("M-2", "user2@example.com", "pw");
        MemberRepository.addMember(member);
        LoanRepository.save(new domain.Loan("ISBN-1", member.getUserName(),
                LocalDate.now(), LocalDate.now().plusDays(7)));
        boolean result = adminActions.canBeUnregistered(member);
        assertFalse(result, "Member with active loans should not be unregisterable");
    }

    @Test
    void canBeUnregisteredWithUnpaidFines_ShouldReturnFalse() {
        Member member = new Member("M-3", "user3@example.com", "pw");
        member.addMemberFine(10.0);
        boolean result = adminActions.canBeUnregistered(member);
        assertFalse(result, "Member with unpaid fines should not be unregisterable");
    }
    // ===========================================================

    // ================= unregisterMember Tests =================
    @Test
    void unregisterMemberWithEligibleMember_ShouldRemove() {
        Member member = new Member("M-4", "user4@example.com", "pw");
        MemberRepository.addMember(member);
        adminActions.unregisterMember(existingAdmin, member.getId());
        assertNull(MemberRepository.findById(member.getId()), "Member should be removed");
    }

    @Test
    void unregisterMemberWithNonAdmin_ShouldThrow() {
        Admin notAdmin = new Admin("Ghost", "pw");
        Member member = new Member("M-5", "user5@example.com", "pw");
        MemberRepository.addMember(member);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminActions.unregisterMember(notAdmin, member.getId()));
        assertEquals("Admin must be logged in to unregister members.", ex.getMessage());
        assertNotNull(MemberRepository.findById(member.getId()), "Member should remain in repository");
    }
    // ===========================================================
    
 // ================= borrowMedia Tests =================
    @Test
    void borrowMediaWithValidAdmin_ShouldCreateLoan() {
        Member member = new Member("user100@example.com", "pw");
        MemberRepository.addMember(member);

        domain.Book book = new domain.Book("ISBN-100", "Title100", "Author100");
        persistence.BookRepository.addBook(book);

        Loan loan = adminActions.borrowMedia(existingAdmin, member, book, LocalDate.now());

        assertNotNull(loan, "Loan object should be returned");
        assertTrue(book.isBorrowed(), "Book should be marked as borrowed");
        assertEquals(member.getUserName(), loan.getMemberId(), "Loan should belong to correct member");
    }

    @Test
    void borrowMediaWithNonAdmin_ShouldThrow() {
        Admin notAdmin = new Admin("Ghost", "pw");
        Member member = new Member("borrow2@example.com", "pw");

        domain.Book book = new domain.Book("ISBN-200", "T", "A");
        persistence.BookRepository.addBook(book);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminActions.borrowMedia(notAdmin, member, book, LocalDate.now()));

        assertEquals("Admin must be logged in to borrow media.", ex.getMessage());
        assertFalse(book.isBorrowed(), "Book should not be marked borrowed");
    }

    // ================= returnMedia Tests =================
    @Test
    void returnMediaWithValidAdmin_ShouldMarkReturned() {
        Member member = new Member("user300@example.com", "pw");
        MemberRepository.addMember(member);

        domain.Book book = new domain.Book("ISBN-300", "T", "A");
        persistence.BookRepository.addBook(book);

        // Create loan manually
        Loan loan = new Loan(book, member.getUserName(), LocalDate.now());
        LoanRepository.save(loan);

        adminActions.returnMedia(existingAdmin, loan, LocalDate.now());

        assertTrue(loan.isReturned(), "Loan should be marked returned");
        assertFalse(book.isBorrowed(), "Book should no longer be marked borrowed");
    }

    @Test
    void returnMediaWithNonAdmin_ShouldThrow() {
        Admin notAdmin = new Admin("Ghost", "pw");
        Member member = new Member("user301@example.com", "pw");

        domain.Book book = new domain.Book("ISBN-301", "T", "A");
        Loan loan = new Loan(book, member.getUserName(), LocalDate.now());
        LoanRepository.save(loan);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminActions.returnMedia(notAdmin, loan, LocalDate.now()));

        assertEquals("Admin must be logged in to return media.", ex.getMessage());
        assertFalse(loan.isReturned(), "Loan should remain active");
    }

    // ================= calculateMemberFineSummary Tests =================
    @Test
    void calculateMemberFineSummary_ShouldSumAllFines() {
        Member member = new Member("user400@example.com", "pw");
        MemberRepository.addMember(member);

        domain.Book b1 = new domain.Book("ISBN-401", "T", "A");
        domain.Book b2 = new domain.Book("ISBN-402", "T2", "A2");
        persistence.BookRepository.addBook(b1);
        persistence.BookRepository.addBook(b2);

        LocalDate oldDate = LocalDate.now().minusDays(40);

        // Two overdue loans
        Loan l1 = new Loan(b1, member.getUserName(), oldDate);
        Loan l2 = new Loan(b2, member.getUserName(), oldDate);

        LoanRepository.save(l1);
        LoanRepository.save(l2);

        double fine = adminActions.calculateMemberFineSummary(existingAdmin, member, LocalDate.now());

        assertTrue(fine > 0, "Total fine should be greater than zero for overdue loans");
    }

    @Test
    void calculateMemberFineSummaryWithNonAdmin_ShouldThrow() {
        Admin notAdmin = new Admin("Ghost", "pw");
        Member member = new Member("user401@example.com", "pw");
        MemberRepository.addMember(member);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> adminActions.calculateMemberFineSummary(notAdmin, member, LocalDate.now()));

        assertEquals("Admin must be logged in to view fine summaries.", ex.getMessage());
    }
    
    @Test
    void registerMember_ShouldThrow_WhenMemberServiceFails() {
       
        Member bad = new Member("", ""); 

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> adminActions.registerMember(existingAdmin, bad)
        );

        assertEquals("Failed to register member (maybe already exists or invalid).", ex.getMessage());
    }
    
    @Test
    void unregisterMember_ShouldThrow_WhenMemberNotFound() {
        String fakeId = "NO_SUCH_ID";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> adminActions.unregisterMember(existingAdmin, fakeId)
        );

        assertEquals("Member not found.", ex.getMessage());
    }
    
    @Test
    void unregisterMember_ShouldThrow_WhenMemberHasActiveLoansOrFines() {
        Member member = new Member("M-X", "x@example.com", "pw");
        MemberRepository.addMember(member);

      
        LoanRepository.save(new Loan("ISBN-X", member.getUserName(),
                LocalDate.now(), LocalDate.now().plusDays(7)));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> adminActions.unregisterMember(existingAdmin, member.getId())
        );

        assertEquals("Cannot unregister member with active loans or unpaid fines.", ex.getMessage());
    }
    
    @Test
    void searchMemberById_WithValidAdminAndExistingMember_ShouldReturnNullButCallService() {
        Member m = new Member("M-500", "user500@example.com", "pw");
        MemberRepository.addMember(m);

        // This function always returns null but must NOT throw
        assertDoesNotThrow(() -> adminActions.searchMemberById(existingAdmin, m.getId()));
    }

    @Test
    void searchMemberById_WithOfflineAdmin_ShouldReturnNull() {
        existingAdmin.setStatus(UserStatus.OFFLINE);
        assertNull(adminActions.searchMemberById(existingAdmin, "ANY"));
    }

    @Test
    void searchMemberById_WithNullId_ShouldReturnNull() {
        assertNull(adminActions.searchMemberById(existingAdmin, null));
    }

    @Test
    void borrowMediaTestDuration_WithValidAdmin_ShouldCreateLoan() {
        Member member = new Member("user700@example.com", "pw");
        MemberRepository.addMember(member);

        domain.Book book = new domain.Book("ISBN-700", "T", "A");
        persistence.BookRepository.addBook(book);

        Loan loan = adminActions.borrowMediaTestDuration(
                existingAdmin, member, book, LocalDate.now(),
                1, 2, 3, 4
        );

        assertNotNull(loan);
        assertTrue(book.isBorrowed());
    }

    @Test
    void borrowMediaTestDuration_WithNonAdmin_ShouldThrow() {
        Admin notAdmin = new Admin("Ghost", "pw");
        Member member = new Member("user701@example.com", "pw");
        domain.Book book = new domain.Book("ISBN-701", "T1", "A1");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> adminActions.borrowMediaTestDuration(
                        notAdmin, member, book, LocalDate.now(),
                        1, 0, 0, 0
                )
        );

        assertEquals(
                "Admin must be logged in to borrow media in testing mode.",
                ex.getMessage()
        );
    }

    @Test
    void borrowMediaTestDuration_WithNullMember_ShouldThrow() {
        domain.Book book = new domain.Book("ISBN-702", "T2", "A2");

        assertThrows(
                IllegalArgumentException.class,
                () -> adminActions.borrowMediaTestDuration(
                        existingAdmin, null, book, LocalDate.now(), 1, 0, 0, 0
                )
        );
    }

    @Test
    void borrowMediaTestDuration_WithNullMedia_ShouldThrow() {
        Member member = new Member("user702@example.com", "pw");

        assertThrows(
                IllegalArgumentException.class,
                () -> adminActions.borrowMediaTestDuration(
                        existingAdmin, member, null, LocalDate.now(), 1, 0, 0, 0
                )
        );
    }

    @Test
    void sendEmailToMember_WithValidAdmin_ShouldSend() {
        EmailService email = mock(EmailService.class);

        AdminActions actions = new AdminActions(
                new MemberService(new MemberRepository()),
                new LoanService(new BorrowingRules(), new LoanRepository()),
                email
        );

        Member m = new Member("user600@example.com", "pw");

        actions.sendEmailToMember(existingAdmin, m, "Hello", "Text");

        verify(email, times(1))
                .sendEmail("user600@example.com", "Hello", "Text");
    }

    @Test
    void sendEmailToMember_ShouldThrow_WhenEmailServiceNull() {
        AdminActions actions = new AdminActions(
                new MemberService(new MemberRepository()),
                new LoanService(new BorrowingRules(), new LoanRepository()),
                null  
        );

        Member m = new Member("user601@example.com", "pw");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> actions.sendEmailToMember(existingAdmin, m, "S", "B")
        );

        assertEquals("Email service is not configured.", ex.getMessage());
    }

    @Test
    void sendEmailToMember_ShouldThrow_WhenMemberInvalid() {
        EmailService email = mock(EmailService.class);

        AdminActions actions = new AdminActions(
                new MemberService(new MemberRepository()),
                new LoanService(new BorrowingRules(), new LoanRepository()),
                email
        );

        Member bad = new Member(null, "pw");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> actions.sendEmailToMember(existingAdmin, bad, "S", "B")
        );

        assertEquals("Valid member is required.", ex.getMessage());
    }


}