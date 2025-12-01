package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
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
    // =====================================================
}