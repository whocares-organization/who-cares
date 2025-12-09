package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.AdminFileLoader;
import domain.Admin;

class AdminFileLoaderTest {

    private AdminFileLoader existingFile;
    private AdminFileLoader notExistingFile;

    // ====== Subclass for simulating read error inside try-block ======
    static class AdminFileLoaderThrowing extends AdminFileLoader {

        public AdminFileLoaderThrowing(String fileName) {
            super(fileName);
        }

        @Override
        protected BufferedReader createBufferedReader(final InputStream inputStream) {
            return new BufferedReader(new InputStreamReader(inputStream)) {
                @Override
                public String readLine() throws IOException {
                    // Simulate an I/O error during reading
                    throw new IOException("Simulated read error");
                }
            };
        }
    }
    // ================================================================

    // ================= Setup & Teardown =================
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        existingFile = new AdminFileLoader("admins.txt");
        notExistingFile = new AdminFileLoader("Sorry_No");
    }

    @AfterEach
    void tearDown() throws Exception {
        existingFile = null;
        notExistingFile = null;
    }
    // ====================================================

    // ================= Load Admins Tests =================
    @Test
    void givenValidFile_whenLoadAdmins_thenReturnListOfAdmins() throws Exception {
        List<Admin> existingAdmins = existingFile.loadAdmins();
        assertFalse(existingAdmins.isEmpty(), "Admins list should not be empty");

        Admin first = existingAdmins.get(0);
        assertEquals("Mohammad", first.getUserName());
        assertEquals("12345", first.getPassword());
    }

    @Test
    void givenMissingFile_whenLoadAdmins_thenThrowException() {
        Exception exception = assertThrows(IOException.class, () -> notExistingFile.loadAdmins());
        assertEquals("Error reading file: Sorry_No", exception.getMessage());
    }

    /**
     * Extra test to cover the branch where a line in the file
     * does not contain both username and password (parts.length < 2).
     *
     * File example (admins_with_invalid_line.txt):
     *   Mohammad,12345
     *   BadLineWithoutPassword
     *   Sara,99999
     */
    @Test
    void givenFileWithInvalidLine_whenLoadAdmins_thenSkipInvalidLine() throws Exception {
        AdminFileLoader loader = new AdminFileLoader("admins_with_invalid_line.txt");

        List<Admin> admins = loader.loadAdmins();

        assertEquals(2, admins.size(), "Should load only valid admin lines");
        assertEquals("Mohammad", admins.get(0).getUserName());
        assertEquals("Sara", admins.get(1).getUserName());
    }

    /**
     * Extra test to cover the catch block:
     * Simulate an IOException during readLine(),
     * so that the catch (IOException e) branch is executed.
     */
    @Test
    void givenReadError_whenLoadAdmins_thenWrapIOExceptionWithFileName() {
        AdminFileLoader loader = new AdminFileLoaderThrowing("admins.txt");

        IOException ex = assertThrows(IOException.class, () -> loader.loadAdmins());

        assertEquals("Error reading file: admins.txt", ex.getMessage());
        assertNotNull(ex.getCause(), "Wrapped exception should have a cause");
        assertTrue(ex.getCause() instanceof IOException, "Cause should be the original IOException");
    }
    // =====================================================
}
