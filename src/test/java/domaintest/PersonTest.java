package domaintest;

import static org.junit.jupiter.api.Assertions.*;

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

    // =============================
    // Constructors
    // =============================

    @Test
    void testDefaultConstructor() {
        Person p = new TestPerson();
        assertNotNull(p);
        assertNull(p.getUserName());
        assertNull(p.getPassword());
        assertNull(p.getId());
    }

    @Test
    void testConstructorUserNamePassword() {
        Person p = new TestPerson("user1", "pass1");
        assertEquals("user1", p.getUserName());
        assertEquals("pass1", p.getPassword());
    }

    @Test
    void testConstructorIdUserNamePassword() {
        Person p = new TestPerson("ID123", "user2", "pass2");
        assertEquals("ID123", p.getId());
        assertEquals("user2", p.getUserName());
        assertEquals("pass2", p.getPassword());
    }

    @Test
    void testFullConstructor() {
        Person p = new TestPerson("user3", "pass3", "Ahmad", "ID999", "12345");
        assertEquals("user3", p.getUserName());
        assertEquals("pass3", p.getPassword());
        assertEquals("Ahmad", p.getName());
        assertEquals("ID999", p.getId());
        assertEquals("12345", p.getPhone());
        assertNotNull(p.getCreatedAt());  // createdAt auto populated
    }

    // =============================
    // Getters & Setters
    // =============================

    @Test
    void testUserNameSetterGetter() {
        Person p = new TestPerson();
        p.setUserName("newUser");
        assertEquals("newUser", p.getUserName());
    }

    @Test
    void testPasswordSetterGetter() {
        Person p = new TestPerson();
        p.setPassword("newPass");
        assertEquals("newPass", p.getPassword());
    }

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

    @Test
    void testCheckPassword_FalseWhenPasswordNull() {
        Person p = new TestPerson("user", null);
        assertFalse(p.checkPassword("anything"));
    }
}
