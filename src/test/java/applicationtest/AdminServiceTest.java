package applicationtest;

import application.AdminFileLoader;
import application.AdminService;
import domain.Admin;
import domain.UserStatus;
import persistence.AdminRepository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminServiceTest {

    private AdminService adminService;
    private AdminRepository repository; 

    @BeforeEach
    void setUp() throws Exception {
        repository = new AdminRepository();
        adminService = new AdminService(repository);
        adminService.loadAdmins(new AdminFileLoader("admins.txt"));
    }

    @AfterEach
    void tearDown() throws Exception {
        repository.clearAdmins();
        adminService = null;
    }

    // ================= Login Tests =================
    @Test
    void loginWithCorrectCredentials_ShouldSucceed() {
        boolean validLogin = adminService.login("Mohammad", "12345");
        assertTrue(validLogin, "Login should succeed with correct credentials");
    }

    @Test
    void loginWithWrongPassword_ShouldFail() {
        boolean validLogin = adminService.login("Mohammad", "wrongpass");
        assertFalse(validLogin, "Login should fail with wrong password");
    }

    @Test
    void loginWithNonExistingUser_ShouldFail() {
        boolean validLogin = adminService.login("Montaser", "12345");
        assertFalse(validLogin, "Login should fail for non-existing user");
    }
    // =================================================

    // ================= Logout Tests =================
    @Test
    void logoutNonExistingUser_ShouldReturnFalse() {
        Boolean result = adminService.logout("Montaser");
        assertFalse(result, "Logout should fail for non-existing user");
    }

    @Test
    void logoutWithEmptyUsername_ShouldReturnNull() {
        Boolean result = adminService.logout("");
        assertNull(result, "Logout should return null for empty username");
    }

    @Test
    void logoutWithNullUsername_ShouldReturnNull() {
        Boolean result = adminService.logout(null);
        assertNull(result, "Logout should return null for null username");
    }

    @Test
    void logoutOfflineUser_ShouldFail() {
        Admin admin = adminService.findAdminByEmail("Mohammad");
        admin.setStatus(UserStatus.OFFLINE);
        Boolean result = adminService.logout("Mohammad");
        assertFalse(result, "Logout should fail for offline user");
        assertEquals(UserStatus.OFFLINE, admin.getStatus(), "Admin status should remain OFFLINE");
    }

    @Test
    void logoutOnlineUser_ShouldSucceed() {
        Admin admin = adminService.findAdminByEmail("Mohammad");
        admin.setStatus(UserStatus.ONLINE);
        Boolean result = adminService.logout("Mohammad");
        assertTrue(result, "Logout should succeed for online user");
        assertEquals(UserStatus.OFFLINE, admin.getStatus(), "Admin status should become OFFLINE");
    }
    // =================================================

    // ================= Register Admin Tests =================
    @Test
    void registerNullAdmin_ShouldReturnNull() {
        Boolean result = adminService.adminRegister(null);
        assertNull(result, "Registering null admin should return null");
    }

    @Test
    void registerExistingAdmin_ShouldReturnFalse() {
        Admin existing = new Admin("Mohammad", "12345");
        Boolean result = adminService.adminRegister(existing);
        assertFalse(result, "Registering existing admin should return false");
    }

    @Test
    void registerAdminWithEmptyUsername_ShouldReturnNull() {
        Admin admin = new Admin("", "1234567");
        Boolean result = adminService.adminRegister(admin);
        assertNull(result, "Admin with empty username should return null");
    }

    @Test
    void registerAdminWithEmptyPassword_ShouldReturnNull() {
        Admin admin = new Admin("Majd", "");
        Boolean result = adminService.adminRegister(admin);
        assertNull(result, "Admin with empty password should return null");
    }

    @Test
    void registerAdminWithNullUsername_ShouldReturnNull() {
        Admin admin = new Admin(null, "1234567");
        Boolean result = adminService.adminRegister(admin);
        assertNull(result, "Admin with null username should return null");
    }

    @Test
    void registerAdminWithNullPassword_ShouldReturnNull() {
        Admin admin = new Admin("Majd", null);
        Boolean result = adminService.adminRegister(admin);
        assertNull(result, "Admin with null password should return null");
    }

    @Test
    void registerValidAdmin_ShouldSucceed() {
        Admin newAdmin = new Admin("Majd", "1234567");
        Boolean result = adminService.adminRegister(newAdmin);
        assertTrue(result, "Registering a valid admin should succeed");
    }
    // =================================================

    // ================= Remove Admin Tests =================
    @Test
    void removeNullAdmin_ShouldReturnNull() {
        Boolean result = adminService.removeAdmin(null);
        assertNull(result, "Removing null admin should return null");
    }

    @Test
    void removeNonExistingAdmin_ShouldReturnFalse() {
        Admin admin = new Admin("Majd", "1234567");
        Boolean result = adminService.removeAdmin(admin);
        assertFalse(result, "Removing non-existing admin should return false");
    }

    @Test
    void removeExistingAdmin_ShouldSucceed() {
        Admin admin = new Admin("Mohammad", "12345");
        Boolean result = adminService.removeAdmin(admin);
        assertTrue(result, "Removing existing admin should succeed");
    }
    // =================================================

    // ================= Find Admin Tests =================
    @Test
    void findExistingAdmin_ShouldReturnAdmin() {
        Admin admin = adminService.findAdminByEmail("Mohammad");
        assertNotNull(admin, "Existing admin should be found");
        assertEquals("Mohammad", admin.getUserName());
    }

    @Test
    void findNonExistingAdmin_ShouldReturnNull() {
        Admin admin = adminService.findAdminByEmail("Majd");
        assertNull(admin, "Non-existing admin should return null");
    }
    // =================================================

    // ================= Load Admin Tests =================
    @Test
    void loadAdmins_ShouldLoadFromFile() throws Exception {
        AdminRepository repo = new AdminRepository();
        AdminService service = new AdminService(repo);
        service.loadAdmins(new AdminFileLoader("admins.txt"));
        assertNotNull(service.findAdminByEmail("Mohammad"), "Admins should be loaded from file");
    }
    // =================================================
}