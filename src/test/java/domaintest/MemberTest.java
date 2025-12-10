package domaintest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Loan;
import domain.Member;
import domain.Loan;

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

    // ===================== calculateTotalFines Tests =====================

    @Test
    void testCalculateTotalFinesWithNullLoans() {
        double total = member.calculateTotalFines(null, LocalDate.now());
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    void testCalculateTotalFinesNoLoanMatches() {
        member.setId("M001"); 

        Loan nullLoan = null; 

        Loan returnedLoan = mock(Loan.class);
        when(returnedLoan.isReturned()).thenReturn(true); 
        when(returnedLoan.getMemberId()).thenReturn("M001");

        Loan otherMemberLoan = mock(Loan.class);
        when(otherMemberLoan.isReturned()).thenReturn(false); 
        when(otherMemberLoan.getMemberId()).thenReturn("OTHER"); 
        List<Loan> loans = Arrays.asList(nullLoan, returnedLoan, otherMemberLoan);

        double total = member.calculateTotalFines(loans, LocalDate.now());
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    void testCalculateTotalFinesWithMatchingLoans() {
        member.setId("M001"); 

        Loan loan1 = mock(Loan.class);
        when(loan1.isReturned()).thenReturn(false);
        when(loan1.getMemberId()).thenReturn("M001");
        when(loan1.getFineAmount()).thenReturn(10.0);

        Loan loan2 = mock(Loan.class);
        when(loan2.isReturned()).thenReturn(false);
        when(loan2.getMemberId()).thenReturn("M001");
        when(loan2.getFineAmount()).thenReturn(5.0);

        List<Loan> loans = Arrays.asList(loan1, loan2);

        LocalDate today = LocalDate.now();
        double total = member.calculateTotalFines(loans, today);

        assertEquals(15.0, total, 1e-9);

        verify(loan1).calculateFine(today);
        verify(loan2).calculateFine(today);
    }

    @Test
    void testCalculateTotalFinesWithNullMemberId() {
        Loan activeLoan = mock(Loan.class);
        when(activeLoan.isReturned()).thenReturn(false);
        when(activeLoan.getMemberId()).thenReturn("M001");

        List<Loan> loans = Arrays.asList(activeLoan);

        double total = member.calculateTotalFines(loans, LocalDate.now());
        assertEquals(0.0, total, 1e-9);

        verify(activeLoan, never()).calculateFine(any(LocalDate.class));
    }
}
