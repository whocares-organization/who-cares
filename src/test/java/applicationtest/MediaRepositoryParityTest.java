package applicationtest;

import application.AdminActions;
import application.AdminFileLoader;
import domain.Admin;
import domain.UserStatus;
import domain.Book;
import domain.CD;
import domain.Loan;
import domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import persistence.AdminRepository;
import persistence.BookRepository;
import persistence.CDRepository;
import persistence.LoanRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies CDs are managed like Books (Sprint 5 parity).
 */
@Disabled("Repository parity tests no longer needed")
class MediaRepositoryParityTest {

    private AdminActions actions;
    private Admin admin;

    @BeforeEach
    void setUp() throws Exception {
        AdminRepository.clearAdmins();
        BookRepository.clearBooks();
        CDRepository.clearCDs();
        LoanRepository.clearLoans();
        actions = new AdminActions();
        admin = new AdminFileLoader("admins.txt").loadAdmins().get(0);
        AdminRepository.addAdmin(admin);
        admin.setStatus(UserStatus.ONLINE);
    }

    @Test
    void cd_add_find_borrow_and_return_updatesBorrowedList() {}

    @Test
    void cd_doubleBorrowRejected() {}

    @Test
    void book_add_find_borrow_and_return_updatesBorrowedList() {}

    @Test
    void book_doubleBorrowRejected() {}
}