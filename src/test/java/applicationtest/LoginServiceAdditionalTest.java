package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.AdminService;
import application.LoginService;
import application.MemberService;
import domain.Admin;
import domain.Member;
import persistence.AdminRepository;
import persistence.MemberRepository;

class LoginServiceAdditionalTest {

    private AdminService adminService;
    private MemberService memberService;
    private LoginService loginService;

    @BeforeEach
    void setup() {
        AdminRepository.clearAdmins();
        MemberRepository.clearMembers();
        adminService = new AdminService(new AdminRepository());
        memberService = new MemberService(new MemberRepository());
        loginService = new LoginService(adminService, memberService);

        adminService.adminRegister(new Admin("admin@example.com", "secret"));
        memberService.registerMember(new Member("member@example.com", "pw"));
    }

    @Test
    void loginAdminSuccess() {
        assertNotNull(loginService.loginAdmin("admin@example.com", "secret"));
    }

    @Test
    void loginAdminFailureWrongPassword() {
        assertNull(loginService.loginAdmin("admin@example.com", "wrong"));
    }

    @Test
    void loginAdminFailureUnknownUser() {
        assertNull(loginService.loginAdmin("nope@example.com", "secret"));
    }

    @Test
    void loginMemberSuccess() {
        assertNotNull(loginService.loginMember("member@example.com", "pw"));
    }

    @Test
    void loginMemberFailureWrongPassword() {
        assertNull(loginService.loginMember("member@example.com", "bad"));
    }

    @Test
    void loginMemberFailureUnknownUser() {
        assertNull(loginService.loginMember("ghost@example.com", "pw"));
    }
}
