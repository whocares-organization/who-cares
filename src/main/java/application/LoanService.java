package application;

import domain.Book;
import domain.Loan;
import domain.Member;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.Observable; // added

public class LoanService extends Observable { // changed to extend Observable

    private static final Logger LOGGER = Logger.getLogger(LoanService.class.getName());
    private static final int STANDARD_LOAN_DAYS = 28;

    // New: rules and instance repository for rules checks
    private final BorrowingRules borrowingRules;
    private final LoanRepository loanRepository;

    public LoanService() {
        this.borrowingRules = new BorrowingRules();
        this.loanRepository = new LoanRepository();
    }

    public LoanService(BorrowingRules borrowingRules, LoanRepository loanRepository) {
        this.borrowingRules = borrowingRules != null ? borrowingRules : new BorrowingRules();
        this.loanRepository = loanRepository != null ? loanRepository : new LoanRepository();
    }

    public Loan borrow(String isbn, String userName) {
        Member member = getExistingMember(userName);
        // Delegate borrowing validations to rules
        borrowingRules.ensureCanBorrow(member, loanRepository);

        Book book = getExistingBook(isbn);
        ensureBookNotAlreadyBorrowed(isbn);

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(isbn, userName, today, today.plusDays(STANDARD_LOAN_DAYS));

        LoanRepository.save(loan);
        book.setBorrowed(true);

        LOGGER.info("Book borrowed successfully! Due date: " + loan.getDueDate());
        return loan;
    }

    public void returnBook(String isbn, String memberId) {
        Loan loan = LoanRepository.findActiveByMemberAndIsbn(memberId, isbn);
        if (loan == null) {
            LOGGER.info("No active loan found for this book/member.");
            return;
        }

        LoanRepository.markReturned(loan);
        Book book = BookRepository.findBookByIsbn(isbn);
        if (book != null) book.setBorrowed(false);

        LocalDate today = LocalDate.now();
        if (loan.isOverdue(today)) {
            loan.calculateFine(today);
            Member member = MemberRepository.findById(memberId);
            if (member != null) {
                member.addMemberFine(loan.getFineAmount());
                LOGGER.info("Book was overdue. Fine added: " + loan.getFineAmount());
            }
        }

        LOGGER.info("Book returned successfully!");
    }

    public List<Loan> findOverdues(LocalDate today) {
        List<Loan> overdueLoans = LoanRepository.findAllActiveOverdue(today);
        overdueLoans.forEach(loan -> {
            loan.calculateFine(today);
            Member member = MemberRepository.findById(loan.getMemberId());
            if (member != null) {
                member.addMemberFine(loan.getFineAmount());
                LOGGER.info("Fine added to member " + member.getUserName()
                        + ": " + loan.getFineAmount()
                        + " for book ISBN " + loan.getIsbn());
            }
        });

        if (overdueLoans.isEmpty()) {
            LOGGER.info("No overdue books found.");
        }

        return overdueLoans;
    }

    /**
     * Scans repository for loans that are overdue as of given date and not yet notified,
     * updates their fine, and notifies observers exactly once per loan.
     */
    public void scanAndNotifyOverdues(LocalDate today) {
        List<Loan> overdueActive = LoanRepository.findAllActiveOverdue(today);
        for (Loan loan : overdueActive) {
            if (!loan.isOverdueNotificationSent()) {
                loan.calculateFine(today);
                setChanged();
                notifyObservers(loan);
                loan.markOverdueNotificationSent();
            }
        }
    }

    public void showAllLoans() {
        List<Loan> loans = LoanRepository.findAllActive();
        if (loans.isEmpty()) {
            LOGGER.info("No active loans currently.");
            return;
        }

        LOGGER.info("=== Active Loans ===");
        LocalDate today = LocalDate.now();
        loans.forEach(l -> LOGGER.info(
                "ISBN: " + l.getIsbn()
                        + " | Member ID: " + l.getMemberId()
                        + " | Due: " + l.getDueDate()
                        + (l.isOverdue(today) ? " | OVERDUE" : "")
        ));
    }
    
    public List<Loan> getOverdueLoansForMember(String memberId, LocalDate date) {
        if (memberId == null || memberId.isBlank()) return List.of();
        return loanRepository.findActiveOverdueByMember(memberId, date);
    }


    // =========================
    // Validation helpers
    // =========================

    private Member getExistingMember(String userName) {
        Member member = MemberRepository.findMemberByEmail(userName);
        if (member == null) throw new IllegalArgumentException("Member not found");
        return member;
    }

    private Book getExistingBook(String isbn) {
        Book book = BookRepository.findBookByIsbn(isbn);
        if (book == null) throw new IllegalArgumentException("Book not found");
        return book;
    }

    private void ensureBookNotAlreadyBorrowed(String isbn) {
        if (LoanRepository.findActiveByIsbn(isbn) != null) {
            throw new IllegalStateException("Book is already borrowed");
        }
    }
}