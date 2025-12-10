package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Loan;
import domain.Member;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member("user1", "12345");
        member.setId("ID001"); // Needed for calculateTotalFines matching
    }

    // ================================
    // Constructors
    // ================================
    @Test
    void testDefaultConstructor() {
        Member m = new Member();
        assertNotNull(m);
    }

    @Test
    void testConstructorUserNamePassword() {
        Member m = new Member("user2", "pass");
        assertEquals("user2", m.getUserName());
        assertEquals("pass", m.getPassword());
        assertEquals(0.0, m.getFineBalance());
    }

    @Test
    void testConstructorIdUserNamePassword() {
        Member m = new Member("id123", "user3", "pass3");
        assertEquals("user3", m.getUserName());
        assertEquals("pass3", m.getPassword());
        assertEquals("id123", m.getId());
        assertEquals(0.0, m.getFineBalance());
    }

    @Test
    void testFullConstructor() {
        Member m = new Member("user4", "pass4", "Ali", "id456", "123456789");
        assertEquals("user4", m.getUserName());
        assertEquals("pass4", m.getPassword());
        assertEquals("Ali", m.getName());
        assertEquals("id456", m.getId());
        assertEquals("123456789", m.getPhone());
        assertEquals(0.0, m.getFineBalance());
    }

    // ================================
    // Fine Balance
    // ================================
    @Test
    void testSetAndGetFineBalance() {
        member.setFineBalance(50.0);
        assertEquals(50.0, member.getFineBalance());
    }

    @Test
    void testAddMemberFine() {
        member.addMemberFine(20.0);
        assertEquals(20.0, member.getFineBalance());
        member.addMemberFine(30.0);
        assertEquals(50.0, member.getFineBalance());
    }

    @Test
    void testPayMemberFinePartial() {
        member.addMemberFine(100.0);
        member.payMemberFine(40.0);
        assertEquals(60.0, member.getFineBalance());
    }

    @Test
    void testPayMemberFineFull() {
        member.addMemberFine(80.0);
        member.payMemberFine(100.0);
        assertEquals(0.0, member.getFineBalance());
    }

    @Test
    void testPayMemberFineInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> member.payMemberFine(0));
        assertThrows(IllegalArgumentException.class, () -> member.payMemberFine(-10));
    }

    // ================================
    // Borrow Eligibility
    // ================================
    @Test
    void testCanBorrow() {
        assertTrue(member.canBorrow());
        member.addMemberFine(10.0);
        assertFalse(member.canBorrow());
    }

    // ================================
    // toString()
    // ================================
    @Test
    void testToString() {
        member.setName("Ali");
        member.addMemberFine(15.0);
        String str = member.toString();
        assertTrue(str.contains("Ali"));
        assertTrue(str.contains("15.0"));
        assertTrue(str.contains("Member"));
    }

    // ================================
    // calculateTotalFines()
    // ================================
    @Test
    void testCalculateTotalFines_NullLoans_ReturnsZero() {
        double result = member.calculateTotalFines(null, LocalDate.now());
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateTotalFines_IgnoresNullLoanEntries() {
        Iterable<Loan> loans = Arrays.asList(null, null);
        double result = member.calculateTotalFines(loans, LocalDate.now());
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateTotalFines_IgnoresReturnedLoans() {
        Loan l = new Loan("ISBN", "ID001", LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        l.setReturned(true);
        Iterable<Loan> loans = Collections.singletonList(l);
        double result = member.calculateTotalFines(loans, LocalDate.now());
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateTotalFines_IgnoresLoansOfOtherMembers() {
        Loan l = new Loan("ISBN", "OTHER_ID", LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        Iterable<Loan> loans = Collections.singletonList(l);
        double result = member.calculateTotalFines(loans, LocalDate.now());
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateTotalFines_IdNull_SkipAllLoans() {
        Member m = new Member("user", "pass");
        m.setId(null);

        Loan l = new Loan("ISBN", "ID001", LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        Iterable<Loan> loans = Collections.singletonList(l);

        double result = m.calculateTotalFines(loans, LocalDate.now());
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateTotalFines_AccumulatesCorrectly() {
        Loan l1 = new Loan("ISBN1", "ID001", LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        Loan l2 = new Loan("ISBN2", "ID001", LocalDate.now().minusDays(7), LocalDate.now().minusDays(3));

        Iterable<Loan> loans = Arrays.asList(l1, l2);

        double total = member.calculateTotalFines(loans, LocalDate.now());
        double expected = l1.getFineAmount() + l2.getFineAmount();

        assertEquals(expected, total);
    }
}
