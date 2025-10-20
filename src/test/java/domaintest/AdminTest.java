package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Admin;

class AdminTest {

	  private Admin admin;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		admin = new Admin("user1", "12345");
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

}
