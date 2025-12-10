package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import domain.Person;
import domain.UserStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonTest {

    // Concrete subclass عشان نقدر نختبر كلاس Person (لأنه abstract)
    private static class TestPerson extends Person {
        public TestPerson() {
            super();
        }
        public TestPerson(String userName, String password) {
            super(userName, password);
        }
        public TestPerson(String id, String userName, String password) {
            super(id, userName, password);
        }
        public TestPerson(String userName, String password, String name, String id, String phone) {
            super(userName, password, name, id, phone);
        }
    }

    @BeforeAll
    static void setUpBeforeClass() throws Exception {}

    @AfterAll
    static void tearDownAfterClass() throws Exception {}

    @BeforeEach
    void setUp() throws Exception {}

    @AfterEach
    void tearDown() throws Exception {}

    // التست الأصلي - خليته زي ما هو
    @Test
    void placeholder() {
        assertTrue(true);
    }

    @Test
    void defaultConstructor_ShouldInitializeFieldsToNull() {
        TestPerson p = new TestPerson();

        assertNull(p.getUserName());
        assertNull(p.getPassword());
        assertNull(p.getName());
        assertNull(p.getId());
        assertNull(p.getPhone());
        assertNull(p.getCreatedAt());
        assertNull(p.getStatus());
    }

    @Test
    void userNamePasswordConstructor_ShouldSetUserNameAndPassword() {
        TestPerson p = new TestPerson("user@example.com", "secret");

        assertEquals("user@example.com", p.getUserName());
        assertEquals("secret", p.getPassword());
        assertNull(p.getId());
        assertNull(p.getName());
        assertNull(p.getPhone());
    }

    @Test
    void idUserNamePasswordConstructor_ShouldSetIdUserNameAndPassword() {
        TestPerson p = new TestPerson("ID-123", "user2@example.com", "pw2");

        assertEquals("ID-123", p.getId());
        assertEquals("user2@example.com", p.getUserName());
        assertEquals("pw2", p.getPassword());
    }

    @Test
    void fullInfoConstructor_ShouldSetAllFieldsAndCreatedAt() {
        TestPerson p = new TestPerson(
                "fulluser@example.com",
                "fullpw",
                "Full Name",
                "ID-999",
                "0599000000"
        );

        assertEquals("fulluser@example.com", p.getUserName());
        assertEquals("fullpw", p.getPassword());
        assertEquals("Full Name", p.getName());
        assertEquals("ID-999", p.getId());
        assertEquals("0599000000", p.getPhone());
        assertNotNull(p.getCreatedAt());
        assertFalse(p.getCreatedAt().isBlank());
    }

    @Test
    void gettersAndSetters_ShouldUpdateAndReturnValuesCorrectly() {
        TestPerson p = new TestPerson();

        p.setUserName("setuser@example.com");
        p.setPassword("setpw");
        p.setName("Set Name");
        p.setId("SET-ID");
        p.setPhone("0599888777");
        p.setCreatedAt("2025-01-01");
        p.setStatus(UserStatus.ONLINE);

        assertEquals("setuser@example.com", p.getUserName());
        assertEquals("setpw", p.getPassword());
        assertEquals("Set Name", p.getName());
        assertEquals("SET-ID", p.getId());
        assertEquals("0599888777", p.getPhone());
        assertEquals("2025-01-01", p.getCreatedAt());
        assertEquals(UserStatus.ONLINE, p.getStatus());
    }

    @Test
    void checkPassword_ShouldReturnTrue_WhenPasswordMatches() {
        TestPerson p = new TestPerson();
        p.setPassword("mypw");

        assertTrue(p.checkPassword("mypw"));
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        TestPerson p = new TestPerson();
        p.setPassword("mypw");

        assertFalse(p.checkPassword("wrongpw"));
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenStoredPasswordIsNull() {
        TestPerson p = new TestPerson(); // password = null

        assertFalse(p.checkPassword("anything"));
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenInputPasswordIsNull() {
        TestPerson p = new TestPerson();
        p.setPassword("notNullPw");

        assertFalse(p.checkPassword(null));
    }
}
