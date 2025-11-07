package applicationtest;

import application.AdminActions;
import application.AdminFileLoader;
import domain.Admin;
import domain.AdminStatus;
import domain.Member;
import persistence.AdminRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AdminActionsTest {

    private AdminActions adminActions;
    private Admin existingAdmin;

    @BeforeEach
    void setUp() throws Exception {
        adminActions = new AdminActions();
        existingAdmin = new AdminFileLoader("admins.txt").loadAdmins().get(0);
        AdminRepository.addAdmin(existingAdmin);
        // Ensure admin is logged in for privileged operations
        existingAdmin.setStatus(AdminStatus.ONLINE);
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
}