package applicationtest;

import application.MemberService;
import domain.Member;
import persistence.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberServiceTest {

    private MemberService memberService;

    @BeforeEach
    void setUp() throws Exception {
        MemberRepository repository = new MemberRepository();
        memberService = new MemberService(repository);

        // Add a sample member
        Member member = new Member("Ali", "Ali1234");
        memberService.registerMember(member);
    }

    @AfterEach
    void tearDown() throws Exception {
        memberService.getRepository().clearMembers();
        memberService = null;
    }

    // ================= Register Member Tests =================
    @Test
    void registerNullMember_ShouldReturnNull() {
        Boolean result = memberService.registerMember(null);
        assertNull(result, "Registering null member should return null");
    }

    @Test
    void registerExistingMember_ShouldReturnFalse() {
        Member existing = new Member("Ali", "Ali1234");
        Boolean result = memberService.registerMember(existing);
        assertFalse(result, "Registering existing member should return false");
    }

    @Test
    void registerMemberWithEmptyUsername_ShouldReturnNull() {
        Member member = new Member("", "password");
        Boolean result = memberService.registerMember(member);
        assertNull(result, "Member with empty username should return null");
    }

    @Test
    void registerMemberWithEmptyPassword_ShouldReturnNull() {
        Member member = new Member("Majd", "");
        Boolean result = memberService.registerMember(member);
        assertNull(result, "Member with empty password should return null");
    }

    @Test
    void registerMemberWithNullUsername_ShouldReturnNull() {
        Member member = new Member(null, "password");
        Boolean result = memberService.registerMember(member);
        assertNull(result, "Member with null username should return null");
    }

    @Test
    void registerMemberWithNullPassword_ShouldReturnNull() {
        Member member = new Member("Majd", null);
        Boolean result = memberService.registerMember(member);
        assertNull(result, "Member with null password should return null");
    }

    @Test
    void registerValidMember_ShouldSucceed() {
        Member newMember = new Member("Majd", "1234567");
        Boolean result = memberService.registerMember(newMember);
        assertTrue(result, "Registering a valid member should succeed");
    }
    // =================================================

    // ================= Remove Member Tests =================
    @Test
    void removeNullMember_ShouldReturnNull() {
        Boolean result = memberService.removeMember(null);
        assertNull(result, "Removing null member should return null");
    }

    @Test
    void removeNonExistingMember_ShouldReturnFalse() {
        Member member = new Member("Montaser", "pass123");
        Boolean result = memberService.removeMember(member);
        assertFalse(result, "Removing non-existing member should return false");
    }

    @Test
    void removeExistingMember_ShouldSucceed() {
        Member member = new Member("Ali", "Ali1234");
        Boolean result = memberService.removeMember(member);
        assertTrue(result, "Removing existing member should succeed");
    }
    // =================================================

    // ================= Find Member Tests =================
    @Test
    void findExistingMember_ShouldReturnMember() {
        Member member = memberService.findMemberByEmail("Ali");
        assertNotNull(member, "Existing member should be found");
        assertEquals("Ali", member.getUserName());
    }
    
    @Test
    void findMemberByEmail_nullOrEmpty_ShouldReturnNull() {
        Member memberNull = memberService.findMemberByEmail(null);
        assertNull(memberNull, "Searching with null should return null");

        Member memberEmpty = memberService.findMemberByEmail("");
        assertNull(memberEmpty, "Searching with empty string should return null");

        Member memberBlank = memberService.findMemberByEmail("   ");
        assertNull(memberBlank, "Searching with blank spaces should return null");
    }

    @Test
    void getAllMembers() {
        memberService.getAllMembers();
    }

    @Test
    void findNonExistingMember_ShouldReturnNull() {
        Member member = memberService.findMemberByEmail("Majd");
        assertNull(member, "Non-existing member should return null");
    }
    // =================================================

    // ================= Login Tests =================
    @Test
    void loginWithCorrectCredentials_ShouldSucceed() {
        boolean validLogin = memberService.login("Ali", "Ali1234");
        assertTrue(validLogin, "Login should succeed with correct credentials");
    }

    @Test
    void loginWithWrongPassword_ShouldFail() {
        boolean validLogin = memberService.login("Ali", "wrongpass");
        assertFalse(validLogin, "Login should fail with wrong password");
    }

    @Test
    void loginWithNonExistingUser_ShouldFail() {
        boolean validLogin = memberService.login("Montaser", "12345");
        assertFalse(validLogin, "Login should fail for non-existing user");
    }
    // =================================================

    // ================= Logout Tests =================
    @Test
    void logoutNonExistingUser_ShouldReturnFalse() {
        Boolean result = memberService.logout("Montaser");
        assertFalse(result, "Logout should fail for non-existing user");
    }

    @Test
    void logoutWithEmptyUsername_ShouldReturnFalse() {
        Boolean result = memberService.logout("");
        assertFalse(result, "Logout should fail for empty username");
    }

    @Test
    void logoutWithNullUsername_ShouldReturnFalse() {
        Boolean result = memberService.logout(null);
        assertFalse(result, "Logout should fail for null username");
    }

    @Test
    void logoutExistingMember_ShouldSucceed() {
        Boolean result = memberService.logout("Ali");
        assertTrue(result, "Logout should succeed for existing member");
    }
    // =================================================
}
