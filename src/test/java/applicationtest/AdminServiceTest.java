package applicationtest;

import application.AdminFileLoader;
import application.AdminService;
import domain.Admin;
import domain.AdminStatus;
import persistence.AdminRepository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminServiceTest {
	private AdminService adminService; 

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		
	}

	@BeforeEach
	void setUp() throws Exception {
		adminService = new AdminService();
	    adminService.loadAdmins(new AdminFileLoader("admins.txt"));
	}

	@AfterEach
	void tearDown() throws Exception {
		adminService = null;
	}

	// ================= Login Tests =================
	@Test
	void loginSuccess() {
		String userName = "Mohammad";
		String Password = "12345";
		
		boolean validLogin = adminService.login(userName, Password);
		assertTrue(validLogin, "Login should succeed with correct username and password");
	}
	
	@Test
	void loginFailWrongPassword() {
		String userName = "Mohammad";
		String Password = "123456";
		
		boolean validLogin = adminService.login(userName, Password);
		assertFalse(validLogin, "Login should fail with wrong password");
	}
	
	@Test
	void loginFailNoAdmin() {
		String userName = "Montaser";
		String Password = "12345";
		
		boolean validLogin = adminService.login(userName, Password);
		assertFalse(validLogin, "Login should fail for non-existing user");
	}
	// ================= Login Tests =================
	
	
	// ================= Logout Tests =================
	@Test
	void logoutWithNoExistingAdmin() {
		String userName = "Montaser";
		
		Boolean validLogout = adminService.logout(userName);
		assertFalse(validLogout, "Logout should fail for non-existing user");
	}
	
	@Test
	void logoutWithEmptyUsername() {
		String userName = "";
		
		Boolean validLogout = adminService.logout(userName);
		assertNull(validLogout, "Logout should fail for empty username");
	}
	
	@Test
	void logoutWithNullUsername() {
		String userName = null;
		
		Boolean validLogout = adminService.logout(userName);
		assertNull(validLogout, "Logout should fail for null username");
	}
	
	@Test
	void logoutWithOfflineStatus() {
		String userName = "Mohammad";
		Admin offlineAdmin = adminService.findAdminByEmail(userName);
		offlineAdmin.setStatus(AdminStatus.OFFLINE);
		
		Boolean validLogout = adminService.logout(userName);
		assertFalse(validLogout, "Logout should fail for offline user");
		assertEquals(AdminStatus.OFFLINE, offlineAdmin.getStatus(), "Admin should remain OFFLINE");
	}
	
	@Test
	void logoutSuccess() {
		String userName = "Mohammad";
		Admin offlineAdmin = adminService.findAdminByEmail(userName);
		offlineAdmin.setStatus(AdminStatus.ONLINE);
		
		Boolean validLogout = adminService.logout(userName);
		assertTrue(validLogout, "Can not logout!");
		assertEquals(AdminStatus.OFFLINE, offlineAdmin.getStatus(), "Admin should become OFFLINE");
	}
	// ================= Logout Tests =================
	
	
	// ================= Register Admin Tests =================
	@Test
	void registerNullAdmin() {
		Admin nullAdmin = null;
		Boolean registerResult = adminService.adminRegister(nullAdmin);
		assertNull(registerResult, "Registering null admin should return null");
	}
	
	@Test
	void registerExistingAdmin() {
		Admin existingAdmin = new Admin("Mohammad", "12345");
		Boolean registerResult = adminService.adminRegister(existingAdmin);
		assertFalse(registerResult, "Registering existing admin should return false");
	}
	
	@Test
	void registerAdminWithoutUsername() {
	    Admin adminWithoutUsername = new Admin("", "1234567");
	    Boolean registerResult = adminService.adminRegister(adminWithoutUsername);
	    assertNull(registerResult, "Registering an admin with empty username should return null");
	}
	
	@Test
	void registerAdminWithoutPassword() {
	    Admin adminWithoutPassword = new Admin("Majd", "");
	    Boolean registerResult = adminService.adminRegister(adminWithoutPassword);
	    assertNull(registerResult, "Registering an admin with empty password should return null");
	}
	
	@Test
	void registerAdminWithNullUsername() {
	    Admin adminWithNullUsername = new Admin(null, "1234567");
	    Boolean registerResult = adminService.adminRegister(adminWithNullUsername);
	    assertNull(registerResult, "Registering an admin with null username should return null");
	}

	@Test
	void registerAdminWithNullPassword() {
	    Admin adminWithNullPassword = new Admin("Majd", null);
	    Boolean registerResult = adminService.adminRegister(adminWithNullPassword);
	    assertNull(registerResult, "Registering an admin with null password should return null");
	}
	
	@Test
	void registerAdminSuccessfully() {
		Admin newadmin = new Admin("Majd", "1234567");
		Boolean registerResult = adminService.adminRegister(newadmin);
		assertTrue(registerResult, "Registering a valid admin should return true");
	}
	// ================= Register Admin Tests =================
	
	// ================= Remove Admin Tests =================
	@Test
	void removeNullAdmin() {
		Admin nullAdmin = null;
		Boolean registerResult = adminService.removeAdmin(nullAdmin);
		assertNull(registerResult, "Removing null admin should return null");
	}
	
	@Test
	void removeNotExistingAdmin() {
		Admin existingAdmin = new Admin("Majd", "1234567");
		Boolean registerResult = adminService.removeAdmin(existingAdmin);
		assertFalse(registerResult, "removing non-existing admin should return false");
	}
	
	@Test
	void removeAdminSuccessfully() {
		Admin deleteAdmin = new Admin("Mohammad", "12345");
		Boolean registerResult = adminService.removeAdmin(deleteAdmin);
		assertTrue(registerResult, "Removing a valid admin should return true");
	}
	// ================= Remove Admin Tests =================
	
	// ================= Find Admin Tests =================
	@Test
	void findExistingAdmin() {
		 Admin actualAdmin = adminService.findAdminByEmail("Mohammad");
		 assertNotNull(actualAdmin, "Admin should exist");
		 assertEquals("Mohammad", actualAdmin.getUsername());
	}
	
	@Test
	void findNotExistingAdmin() {
		Admin actualAdmin = adminService.findAdminByEmail("Majd");
	    assertNull(actualAdmin, "Admin should not exist");
	}
	// ================= Find Admin Tests =================
	
	
	// ================= Load Admin Tests (not important, because i already test it in @BeforeEach =================
	@Test
	void loadAdminsTestIndependently() {
	    AdminRepository repo = new AdminRepository();
	    AdminService service = new AdminService(repo);
	    service.loadAdmins(new AdminFileLoader("admins.txt"));
	    assertNotNull(service.findAdminByEmail("Mohammad"));
	}
	// ================= Load Admin Tests (not important, because i already test it in @BeforeEach =================
}
