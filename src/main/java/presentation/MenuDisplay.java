package presentation;

import domain.Member;
import domain.Book;
import domain.Loan;

import java.util.List;

/**
 * Console UI helper for rendering menus and lists in the Library Management System.
 *
 * <p>This class centralizes all printing logic used by the CLI controller
 * and exposes simple methods for displaying menus, lists, and status messages.</p>
 */
public class MenuDisplay {

    /**
     * Creates a new {@code MenuDisplay} instance.
     */
    public MenuDisplay() { }

    /**
     * Prints the application welcome banner.
     */
    public void showWelcome() {
        System.out.println("==========================================");
        System.out.println("        Library Management System");
        System.out.println("==========================================");
    }

    /**
     * Displays the generic login menu options.
     */
    public void showLoginMenu() {
        System.out.println("\n=== Login Menu ===");
        System.out.println("1) Login as Admin");
        System.out.println("2) Login as Member");
        System.out.println("0) Exit");
    }
    
    /**
     * Displays the admin login prompt header.
     */
    public void showAdminLoginPrompt() {
        System.out.println("\n=== Admin Login ===");
    }
    
    /**
     * Displays the member login prompt header.
     */
    public void showMemberLoginPrompt() {
        System.out.println("\n=== Member Login ===");
    }

    // ------------------- ADMIN MENUS -------------------

    /**
     * Displays the top-level admin menu options.
     */
    public void showAdminMainMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1) Manage Books");
        System.out.println("2) Manage Users");
        System.out.println("3) Borrow/Return Operations");
        System.out.println("4) Overdue Management");
        System.out.println("5) Media (CD) Operations");
        System.out.println("6) Send Manual Email");
        System.out.println("7) Logout");
    }

    /**
     * Displays the admin user-management menu options.
     */
    public void showAdminUserMenu() {
        System.out.println("\n=== Admin User Management ===");
        System.out.println("1) Unregister User");
        System.out.println("2) Register User");
        System.out.println("3) Search User");
        System.out.println("4) View All Users");
        System.out.println("5) View Fine Summary (per member)");
        System.out.println("6) Back");
    }
    
    /**
     * Displays detailed member information for admin review.
     *
     * @param member the member whose information should be displayed
     */
    public void showMemberInformationForAdmin(Member member) {
        System.out.println("\n--- Member Information ---");
        System.out.println("Username: " + member.getUserName());
        System.out.println("Phone: " + member.getPhone());
        System.out.println("Full Name: " + member.getName());
        System.out.println("Fine Balance: " + member.getFineBalance());
        System.out.println("--------------------------");
        
    }
    
    /**
     * Displays the admin book-management menu options.
     */
    public void showAdminBookMenu() {
        System.out.println("\n=== Admin Book Management ===");
        System.out.println("1) Add Book");
        System.out.println("2) Search Book");
        System.out.println("3) Delete Book");
        System.out.println("4) Back");
    }

    /**
     * Displays the admin borrow/return operations menu options.
     */
    public void showAdminBorrowMenu() {
        System.out.println("\n=== Borrow / Return Menu ===");
        System.out.println("1) Borrow Book (on behalf of member)");
        System.out.println("2) Return Book");
        System.out.println("3) Borrow media with advanced custom duration (testing mode)");
        System.out.println("4) Back");
    }

    /**
     * Displays the admin overdue-handling menu options.
     */
    public void showAdminOverdueMenu() {
        System.out.println("\n=== Overdue Management ===");
        System.out.println("1) View Overdue Books/CDs");
        System.out.println("2) Send Reminder Emails");
        System.out.println("3) Back");
    }

    /**
     * Displays the admin media/CD operations menu options.
     */
    public void showAdminMediaMenu() {
        System.out.println("\n=== CD / Media Management ===");
        System.out.println("1) Add CD");
        System.out.println("2) Borrow CD");
        System.out.println("3) Return CD");
        System.out.println("4) Back");
    }

    // ------------------- MEMBER MENUS -------------------

    /**
     * Displays the member menu options.
     */
    public void showMemberMenu() {
        System.out.println("\n=== Member Menu ===");
        System.out.println("1) Search Book");
        System.out.println("2) Borrow Book");
        System.out.println("3) Return Book");
        System.out.println("4) View My Fines");
        System.out.println("5) View My Loans");
        System.out.println("6) Pay Fine");
        System.out.println("7) Logout");
        
    }

    // ------------------- BOOK MENUS -------------------

    /**
     * Displays the book menu options.
     */
    public void showBookMenu() {
        System.out.println("\n=== Book Menu ===");
        System.out.println("1) Add Book");
        System.out.println("2) Search Book");
        System.out.println("3) Back");
    }

    // ------------------- LISTING HELPERS -------------------

    /**
     * Displays a list of books with a simple header and formatting.
     *
     * @param books list of books to render; prints a fallback message if empty or null
     */
    public void listBooks(List<Book> books) {
        System.out.println("\n--- Search Results ---");
        if (books == null || books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        for (Book b : books) {
            System.out.println("- " + b);
        }
    }

    /**
     * Displays a list of members with basic details.
     *
     * @param members list of members to render; prints a fallback message if empty or null
     */
    public void listMembers(List<Member> members) {
        System.out.println("\n--- Members ---");
        if (members == null || members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }
        for (Member m : members) {
            System.out.println("- " + m.getUserName() + " | Name: " + m.getName() + " | Fines: " + m.getFineBalance());
        }
    }

    /**
     * Displays a title and a list of loans with core fields.
     *
     * @param title header to print above the list
     * @param loans the loans to render; prints a fallback message if empty or null
     */
    public void listLoans(String title, List<Loan> loans) {
        System.out.println("\n--- " + title + " ---");
        if (loans == null || loans.isEmpty()) {
            System.out.println("None.");
            return;
        }
        for (Loan l : loans) {
            System.out.println("- Media: " + l.getIsbn() + " | Member: " + l.getMemberId() + " | Due: " + l.getDueDate() + (l.isReturned() ? " | Returned" : ""));
        }
    }

    // ------------------- GENERAL HELPERS -------------------

    /**
     * Prints a success message prefixing a check mark.
     *
     * @param message the message to print
     */
    public void ok(String message) {
        System.out.println("\u2714 " + message);
    }

    /**
     * Prints an error message prefixing a cross mark.
     *
     * @param message the message to print
     */
    public void error(String message) {
        System.out.println("\u274c " + message);
    }
}