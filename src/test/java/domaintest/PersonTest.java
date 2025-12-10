package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import domain.Person;
import domain.UserStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Person;
import domain.UserStatus;

// A simple concrete subclass since Person is abstract
class TestPerson extends Person {
    public TestPerson() { super(); }
    public TestPerson(String user, String pass) { super(user, pass); }
    public TestPerson(String id, String user, String pass) { super(id, user, pass); }
    public TestPerson(String user, String pass, String name, String id, String phone) {
        super(user, pass, name, id, phone);
    }
}

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

    @Test
    void testNameSetterGetter() {
        Person p = new TestPerson();
        p.setName("Sara");
        assertEquals("Sara", p.getName());
    }

    @Test
    void testIdSetterGetter() {
        Person p = new TestPerson();
        p.setId("X001");
        assertEquals("X001", p.getId());
    }

    @Test
    void testPhoneSetterGetter() {
        Person p = new TestPerson();
        p.setPhone("0591234567");
        assertEquals("0591234567", p.getPhone());
    }

    @Test
    void testCreatedAtSetterGetter() {
        Person p = new TestPerson();
        p.setCreatedAt("2025-01-01");
        assertEquals("2025-01-01", p.getCreatedAt());
    }

    @Test
    void testStatusSetterGetter() {
        Person p = new TestPerson();
        p.setStatus(UserStatus.ONLINE);
        assertEquals(UserStatus.ONLINE, p.getStatus());
    }

    // =============================
    // checkPassword()
    // =============================

    @Test
    void testCheckPassword_TrueWhenMatch() {
        Person p = new TestPerson("user", "secret");
        assertTrue(p.checkPassword("secret"));
    }

    @Test
    void testCheckPassword_FalseWhenMismatch() {
        Person p = new TestPerson("user", "secret");
        assertFalse(p.checkPassword("wrong"));
    }

    // التست الأصلي - خليته زي ما هو
    @Test
    void testCheckPassword_FalseWhenPasswordNull() {
        Person p = new TestPerson("user", null);
        assertFalse(p.checkPassword("anything"));
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
