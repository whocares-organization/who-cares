package application;

import domain.Book;
import domain.Loan;
import domain.Member;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling loan-related operations in the library system.
 *
 * <p>This class manages the borrowing and returning of books, tracks overdue loans,
 * calculates fines for late returns, and displays active loan information.
 * It acts as an intermediary between the domain layer and the persistence layer.</p>
 */
public class LoanService {

    /**
     * Allows a library member to borrow a book identified by its ISBN.
     *
     * <p>This method performs several validation checks:
     * <ul>
     *     <li>Ensures the member exists and has no unpaid fines.</li>
     *     <li>Checks that the book exists and is not already borrowed.</li>
     *     <li>Creates a new {@link Loan} record with a 28-day due period.</li>
     * </ul>
     * After a successful borrow, the book's status is updated, and the loan is saved
     * in the repository.</p>
     *
     * @param isbn the ISBN of the book to borrow
     * @param memberId the ID of the member borrowing the book
     * @return the created {@link Loan} object
     * @throws IllegalArgumentException if the member or book cannot be found
     * @throws IllegalStateException if the member has fines or the book is already borrowed
     */
    public Loan borrow(String isbn, String memberId) {
        Member member = MemberRepository.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }
        if (!member.canBorrow()) {
            throw new IllegalStateException("Member has unpaid fines!");
        }

        Book book = BookRepository.findBookByIsbn(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if (LoanRepository.findActiveByIsbn(isbn) != null) {
            throw new IllegalStateException("Book is already borrowed");
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(isbn, memberId, today, today.plusDays(28));
        LoanRepository.save(loan);
        book.setBorrowed(true);

        System.out.println("Book borrowed successfully! Due date: " + loan.getDueDate());
        return loan;
    }

    /**
     * Handles the return of a borrowed book by a member.
     *
     * <p>This method verifies that the book is currently borrowed by the given member.
     * It marks the loan as returned, updates the book's status, and checks for overdue status.
     * If the book is overdue, a fine is calculated and added to the member’s account.</p>
     *
     * @param isbn the ISBN of the returned book
     * @param memberId the ID of the member returning the book
     */
    public void returnBook(String isbn, String memberId) {
        List<Loan> activeLoans = LoanRepository.findActiveByMember(memberId);
        Loan loan = activeLoans.stream()
                .filter(l -> l.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);

        if (loan != null) {
            LoanRepository.markReturned(loan);
            Book book = BookRepository.findBookByIsbn(isbn);
            if (book != null) {
                book.setBorrowed(false);
            }

            LocalDate today = LocalDate.now();
            if (loan.isOverdue(today)) {
                loan.calculateFine(today);
                Member member = MemberRepository.findById(memberId);
                if (member != null) {
                    member.addFine(loan.getFineAmount());
                    System.out.println("Book was overdue. Fine added: " + loan.getFineAmount());
                }
            }

            System.out.println("Book returned successfully!");
        } else {
            System.out.println("No active loan found for this book/member.");
        }
    }

    /**
     * Finds all overdue loans and calculates fines for each one.
     *
     * <p>This method checks all active loans in the {@link LoanRepository},
     * identifies the overdue ones, calculates fines based on the current date,
     * and adds the fine amounts to the corresponding members’ balances.</p>
     *
     * @param today the current date used to determine overdue status
     * @return a list of overdue {@link Loan} objects
     */
    public List<Loan> findOverdues(LocalDate today) {
        List<Loan> overdueLoans = LoanRepository.findAllActive().stream()
                .filter(l -> l.isOverdue(today))
                .collect(Collectors.toList());

        for (Loan loan : overdueLoans) {
            loan.calculateFine(today);
            Member member = MemberRepository.findById(loan.getMemberId());
            if (member != null) {
                member.addFine(loan.getFineAmount());
                System.out.println("Fine added to member " + member.getName() +
                        ": " + loan.getFineAmount() + " for book ISBN " + loan.getIsbn());
            }
        }

        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue books found.");
        }

        return overdueLoans;
    }

    /**
     * Displays all currently active loans.
     *
     * <p>This method retrieves all active loans from the repository and prints their details,
     * including ISBN, member ID, due date, and overdue status if applicable.</p>
     */
    public void showAllLoans() {
        List<Loan> loans = LoanRepository.findAllActive();
        if (loans.isEmpty()) {
            System.out.println("No active loans currently.");
        } else {
            System.out.println("=== Active Loans ===");
            for (Loan l : loans) {
                System.out.println("ISBN: " + l.getIsbn() +
                        " | Member ID: " + l.getMemberId() +
                        " | Due: " + l.getDueDate() +
                        (l.isOverdue(LocalDate.now()) ? " | OVERDUE" : ""));
            }
        }
    }
}
