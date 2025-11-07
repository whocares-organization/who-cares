package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Admin;
import domain.AdminStatus;

class AdminTest {

    private Admin admin;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        System.out.println("Starting Admin tests...");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        System.out.println("All Admin tests done.");
    }

    @BeforeEach
    void setUp() throws Exception {
        admin = new Admin();
        admin.setUserName("user1");
        admin.setPassword("12345");
    }

    @AfterEach
    void tearDown() throws Exception {
        admin = null;
    }

    @Test
    void testCheckPasswordCorrect() {
        assertTrue(admin.checkPassword("12345"));
    }

    @Test
    void testCheckPasswordIncorrect() {
        assertFalse(admin.checkPassword("wrong"));
    }

    @Test
    void testSetAndGetStatus() {
        admin.setStatus(AdminStatus.ONLINE);
        assertEquals(AdminStatus.ONLINE, admin.getStatus());

        admin.setStatus(AdminStatus.OFFLINE);
        assertEquals(AdminStatus.OFFLINE, admin.getStatus());
    }

    @Test
    void testToStringContainsNameAndStatus() {
        admin.setStatus(AdminStatus.ONLINE);
        String result = admin.toString();
        assertTrue(result.contains("Admin"));
        assertTrue(result.contains("ONLINE"));
    }

    @Test
    void testToStringWithNullStatus() {
        admin.setStatus(null);
        String result = admin.toString();
        assertTrue(result.contains("Admin"));
        assertTrue(result.contains("name")); // part of toString()
    }
    
    @Test
    void testFullParameterizedConstructor() {
        Admin fullAdmin = new Admin("adminUser", "pass123", "Admin Name", "ID001", "0591234567");

        assertEquals("adminUser", fullAdmin.getUserName());
        assertTrue(fullAdmin.checkPassword("pass123"));
        assertEquals("Admin Name", fullAdmin.getName());
        assertEquals("ID001", fullAdmin.getId());
        assertEquals("0591234567", fullAdmin.getPhone());
        assertNull(fullAdmin.getStatus(), "Status should be null initially");
    }
    
    @Test
    void testDefaultConstructor() {
        Admin defaultAdmin = new Admin();
        assertNotNull(defaultAdmin);
        assertNull(defaultAdmin.getName(), "Name should be null by default");
        assertNull(defaultAdmin.getPassword(), "Password should be null by default");
        assertNull(defaultAdmin.getStatus(), "Status should be null by default");
    }
}
