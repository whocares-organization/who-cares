package presentation;

import application.AdminService;
import application.BookService;
import application.LoanService;
import application.LoginService;
import application.MemberService;
import application.ReminderService;
import application.AdminActions;
import application.CDService;
import applicationsearchbooks.BookAuthorSearchStrategy;
import applicationsearchbooks.BookIsbnSearchStrategy;
import applicationsearchbooks.BookTitleSearchStrategy;
import domain.Admin;
import domain.Book;
import domain.Loan;
import domain.Member;
import domain.Person;

import java.time.LocalDate;
import java.time.Instant; 
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller orchestrating CLI interactions for admins and members.
 */
public class MenuController {

    private final MenuDisplay menuDisplay;
    private final InputHandler inputHandler;

    private final LoginService loginService;
    private final AdminService adminService;
    private final MemberService memberService;
    private final BookService bookService;
    private final LoanService loanService;
    private final ReminderService reminderService;
    private final AdminActions adminActions;
    private final CDService cdService = new CDService();

    Person p = null; 

    /**
     * Constructs a new controller wiring the display, input, and service dependencies.
     *
     * @param menuDisplay the display helper
     * @param inputHandler the input reader/validator
     * @param loginService the login service
     * @param adminService the admin service
     * @param memberService the member service
     * @param bookService the book service
     * @param loanService the loan service
     * @param reminderService the reminder service
     * @param adminActions admin action helper
     */
    public MenuController(MenuDisplay menuDisplay, InputHandler inputHandler, LoginService loginService,
                          AdminService adminService, MemberService memberService, BookService bookService,
                          LoanService loanService, ReminderService reminderService, AdminActions adminActions) {
        this.menuDisplay = menuDisplay;
        this.inputHandler = inputHandler;
        this.loginService = loginService;
        this.adminService = adminService;
        this.memberService = memberService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.reminderService = reminderService;
        this.adminActions = adminActions;
    }

    /**
     * Returns the menu display helper.
     *
     * @return the display helper
     */
    public MenuDisplay getMenuDisplay() { return menuDisplay; }

    /**
     * Returns the input handler.
     *
     * @return the input handler
     */
    public InputHandler getInputHandler() { return inputHandler; }

    /**
     * Prints the welcome banner.
     */
    public void displayWelcome() { menuDisplay.showWelcome(); }

    /**
     * Starts the main login-and-run loop.
     */
    public void run() {
        displayWelcome();
        while (true) {
            Person who = login();
            if (who == null) {
                menuDisplay.ok("Exiting. Goodbye.");
                break;
            }
            if (who instanceof Admin) {
                adminMainLoop();
            } else if (who instanceof Member) {
                memberMainLoop();
            }
        }
    }

    private Person login() {
        menuDisplay.showLoginMenu();
        
        int choice = inputHandler.readIntRange("Select an option: ", 0, 2);
        if(choice == 0) {
			return null; 
		}
        
        if(choice == 1) {
			menuDisplay.showAdminLoginPrompt();
			String username = inputHandler.readString("Username (email): ");
			String password = inputHandler.readString("Password: ");
			Person admin = loginService.loginAdmin(username, password);
			if (admin == null) {
				menuDisplay.error("Invalid admin credentials.");
				return login();
			}
			menuDisplay.ok("Admin logged in: " + admin.getUserName());
			printAdminStatus(admin);
			p = admin;
			return admin;
		} else if(choice == 2) {
			menuDisplay.showMemberLoginPrompt();
			String username = inputHandler.readString("Username (email): ");
			String password = inputHandler.readString("Password: ");
			Person member = loginService.loginMember(username, password);
			if (member == null) {
				menuDisplay.error("Invalid member credentials.");
				return login();
			}
			menuDisplay.ok("Member logged in: " + member.getUserName());
			p = member;
			return member;
		} else {
			menuDisplay.error("Invalid choice.");
			return login();
		}
    }


	/**
     * Displays and handles the admin user management menu.
     */
    public void adminManageUsersMenu() {
        if (!(p instanceof Admin)) {
            menuDisplay.error("Admin privileges required.");
            return;
        }
        boolean back = false;
        while (!back && p instanceof Admin) {
            menuDisplay.showAdminUserMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 6);
            switch (choice) {
                case 1:
                    try {
                        String memberIdToUnregister = inputHandler.readString("Enter member ID to unregister: ");
                        adminActions.unregisterMember((Admin) p, memberIdToUnregister);
                        menuDisplay.ok("Member unregistered: " + memberIdToUnregister);
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 2:
                    try {
                        String name = inputHandler.readString("Enter member name: ");
                        String username = inputHandler.readString("Enter username (email): ");
                        String password = inputHandler.readString("Enter password: ");
                        String phone = inputHandler.readString("Enter phone number: ");
                        String id = inputHandler.readString("Enter member ID: ");
                        Person newMember = new Member(username, password, name, id, phone);
                        adminActions.registerMember((Admin) p, (Member) newMember);
                        menuDisplay.ok("Member registered: " + username);
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 3: // Search user
                    int how = inputHandler.readIntRange("Search by: 1) ID  2) Email: ", 1, 2);
                    Member found = null;
                    if (how == 1) {
                        String mid = inputHandler.readString("Enter member ID: ");
                        found = memberService.findMemberById(mid);
                    } else {
                        String email = inputHandler.readString("Enter member email: ");
                        found = memberService.findMemberByEmail(email);
                    }
                    if (found != null) {
                        menuDisplay.showMemberInformationForAdmin(found);
                    } else {
                        menuDisplay.error("Member not found.");
                    }
                    break;
                case 4:
                    List<Member> all = memberService.getAllMembers();
                    menuDisplay.listMembers(all);
                    break;
                case 5: 
                    try {
                        String memberEmail = inputHandler.readString("Enter member email/username: ");
                        Member member = memberService.findMemberByEmail(memberEmail);
                        if (member == null) { menuDisplay.error("Member not found."); break; }
                        double total = adminActions.calculateMemberFineSummary((Admin) p, member, LocalDate.now());
                        menuDisplay.ok("Total fines (books+CDs) for " + member.getUserName() + ": " + total);
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 6:
                    back = true;
                    break;
            }
        }
    }

    private void adminMainLoop() {
        while (p instanceof Admin) {
            menuDisplay.showAdminMainMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 7);
            switch (choice) {
                case 1:
                    adminManageBooksMenu();
                    break;
                case 2:
                    adminManageUsersMenu();
                    break;
                case 3:
                    adminBorrowReturnMenu();
                    break;
                case 4:
                    adminOverdueMenu();
                    break;
                case 5:
                    adminMediaMenu();
                    break;
                case 6:
                    adminManualEmailMenu();
                    break;
                case 7:
                    if (p instanceof Admin) {
                        Boolean out = adminService.logout(((Admin) p).getUserName());
                        if (Boolean.TRUE.equals(out)) {
                            menuDisplay.ok("Admin logged out.");
                        } else {
                            menuDisplay.error("Logout failed or already offline.");
                        }
                    }
                    p = null;
                    return;
            }
        }
    }

    private void adminManageBooksMenu() {
        boolean back = false;
        while (!back && p instanceof Admin) {
            menuDisplay.showAdminBookMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 4);
            switch (choice) {
                case 1:
                    String title = inputHandler.readString("Enter title: ");
                    String author = inputHandler.readString("Enter author: ");
                    String isbn = inputHandler.readString("Enter ISBN: ");
                    Book nb = new Book(title, author, isbn);
                    Boolean added = bookService.addBook(nb);
                    if (added == null) menuDisplay.error("Invalid book data.");
                    else if (added) menuDisplay.ok("Book added.");
                    else menuDisplay.error("Book already exists or could not be added.");
                    break;
                case 2:
                    int searchType = inputHandler.readIntRange(
                            "Search by: 1) Title  2) Author  3) ISBN: ", 1, 3);
                    switch (searchType) {
                        case 1:
                            bookService.getRepository().setSearchStrategy(new BookTitleSearchStrategy());
                            break;
                        case 2:
                            bookService.getRepository().setSearchStrategy(new BookAuthorSearchStrategy());
                            break;
                        case 3:
                            bookService.getRepository().setSearchStrategy(new BookIsbnSearchStrategy());
                            break;
                        default:
                            bookService.getRepository().setSearchStrategy(new BookTitleSearchStrategy());
                    }
                    String keyword = inputHandler.readString("Enter keyword: ");
                    List<Book> results = bookService.search(keyword);
                    menuDisplay.listBooks(results);
                    break;
                case 3: 
                    String delIsbn = inputHandler.readString("Enter ISBN to delete: ");
                    Book toDelete = bookService.searchBooks(delIsbn);
                    if (toDelete == null) {
                        menuDisplay.error("Book not found.");
                    } else {
                        Boolean removed = bookService.removeBook(toDelete);
                        if (removed == null) menuDisplay.error("Invalid request.");
                        else if (removed) menuDisplay.ok("Book removed.");
                        else menuDisplay.error("Could not remove book.");
                    }
                    break;
                case 4:
                    back = true;
                    break;
            }
        }
    }

    private void adminBorrowReturnMenu() {
        boolean back = false;
        while (!back && p instanceof Admin) {
            menuDisplay.showAdminBorrowMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 4);
            switch (choice) {
                case 1: 
                    try {
                        String memberEmail = inputHandler.readString("Enter member email/username: ");
                        Member member = memberService.findMemberByEmail(memberEmail);
                        if (member == null) {
                            menuDisplay.error("Member not found.");
                            break;
                        }
                        String isbn = inputHandler.readString("Enter book ISBN: ");
                        Book book = bookService.searchBooks(isbn);
                        if (book == null) {
                            menuDisplay.error("Book not found.");
                            break;
                        }
                        String daysRaw = inputHandler.readOptionalString("Enter custom borrow duration (days) or press Enter for default: ");
                        Integer days = null;
                        if (daysRaw != null && !daysRaw.trim().isEmpty()) {
                            try {
                                days = Integer.parseInt(daysRaw.trim());
                            } catch (NumberFormatException nfe) {
                                menuDisplay.error("Invalid number, using default duration.");
                                days = null;
                            }
                        }
                        Loan loan;
                        if (days == null) {
                            loan = adminActions.borrowMedia((Admin) p, member, book, LocalDate.now());
                        } else {
                            loan = loanService.borrowMedia(member, book, LocalDate.now(), days);
                        }
                        menuDisplay.ok("Borrowed. Due date: " + loan.getDueDate());
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 2:
                    try {
                        String memberEmail = inputHandler.readString("Enter member email/username: ");
                        String isbn = inputHandler.readString("Enter book ISBN: ");
                        loanService.returnBook(isbn, memberEmail);
                        menuDisplay.ok("Book returned (if active loan existed).");
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 3: 
                    try {
                        String memberEmail = inputHandler.readString("Enter member email/username: ");
                        Member member = memberService.findMemberByEmail(memberEmail);
                        if (member == null) {
                            menuDisplay.error("Member not found.");
                            break;
                        }
                        String isbn = inputHandler.readString("Enter book ISBN: ");
                        Book book = bookService.searchBooks(isbn);
                        if (book == null) {
                            menuDisplay.error("Book not found.");
                            break;
                        }

                        int days = inputHandler.readInt("Enter testing days (>=0): ");
                        int hours = inputHandler.readInt("Enter testing hours (>=0): ");
                        int minutes = inputHandler.readInt("Enter testing minutes (>=0): ");
                        int seconds = inputHandler.readInt("Enter testing seconds (>=0): ");

                        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
                            menuDisplay.error("Duration cannot be all zeros in testing mode.");
                            break;
                        }

                        Loan loan = adminActions.borrowMediaTestDuration(
                                (Admin) p,
                                member,
                                book,
                                LocalDate.now(),
                                days,
                                hours,
                                minutes,
                                seconds);

                        long totalSeconds =
                                (long) days * 24 * 60 * 60 +
                                (long) hours * 60 * 60 +
                                (long) minutes * 60 +
                                (long) seconds;
                        String durationDisplay;
                        if (days == 0 && hours == 0 && minutes == 0) {
                            durationDisplay = totalSeconds + " seconds";
                        } else {
                            StringBuilder parts = new StringBuilder();
                            if (days > 0) parts.append(days).append(days == 1 ? " day" : " days");
                            if (hours > 0) {
                                if (parts.length() > 0) parts.append(' ');
                                parts.append(hours).append(hours == 1 ? " hour" : " hours");
                            }
                            if (minutes > 0) {
                                if (parts.length() > 0) parts.append(' ');
                                parts.append(minutes).append(minutes == 1 ? " minute" : " minutes");
                            }
                            if (seconds > 0) {
                                if (parts.length() > 0) parts.append(' ');
                                parts.append(seconds).append(seconds == 1 ? " second" : " seconds");
                            }
                            durationDisplay = parts.length() == 0 ? (totalSeconds + " seconds") : parts.toString();
                        }
                        Instant testingDueAt = loan.getTestingDueDate();
                        StringBuilder confirm = new StringBuilder();
                        confirm.append("[TESTING MODE] Loan registered.\n")
                               .append("    Media ID: ").append(loan.getIsbn()).append('\n')
                               .append("    Member: ").append(member.getUserName()).append('\n')
                               .append("    Testing duration: ").append(durationDisplay).append('\n')
                               .append("    Will expire at: ").append(testingDueAt != null ? testingDueAt.toString() : "(unknown)").append('\n')
                               .append("------------------------------------------------------");
                        menuDisplay.ok(confirm.toString());

                        try { Thread.sleep(300); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 4:
                    back = true;
                    break;
            }
        }
    }

    private void adminOverdueMenu() {
        boolean back = false;
        while (!back && p instanceof Admin) {
            menuDisplay.showAdminOverdueMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 3);
            switch (choice) {
                case 1:
                    List<Loan> overdue = loanService.findOverdues(LocalDate.now());
                    menuDisplay.listLoans("Overdue Loans", overdue);
                    break;
                case 2: 
                    try {
                        reminderService.sendOverdueReminders();
                        menuDisplay.ok("Reminder emails triggered.");
                    } catch (Exception ex) {
                        menuDisplay.error("Failed to send reminders: " + ex.getMessage());
                    }
                    break;
                case 3:
                    back = true;
                    break;
            }
        }
    }

    private void adminMediaMenu() {
        boolean back = false;
        while (!back && p instanceof Admin) {
            menuDisplay.showAdminMediaMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 4);
            switch (choice) {
                case 1:
                    menuDisplay.error("Add CD is not implemented in Phase 1.");
                    break;
                case 2:
                    try {
                        String memberEmail = inputHandler.readString("Enter member email/username: ");
                        Member member = memberService.findMemberByEmail(memberEmail);
                        if (member == null) { menuDisplay.error("Member not found."); break; }
                        String cdId = inputHandler.readString("Enter CD ID: ");
                        Loan loan = cdService.borrowCD(member, cdId, LocalDate.now());
                        menuDisplay.ok("CD borrowed. Due date: " + loan.getDueDate());
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 3:
                    try {
                        String memberEmail = inputHandler.readString("Enter member email/username: ");
                        Member member = memberService.findMemberByEmail(memberEmail);
                        if (member == null) { menuDisplay.error("Member not found."); break; }
                        String cdId = inputHandler.readString("Enter CD ID: ");
                        double fine = cdService.returnCD(member, cdId, LocalDate.now());
                        if (fine > 0) {
                            member.addMemberFine(fine);
                        }
                        menuDisplay.ok("CD returned. Fine: " + fine);
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 4:
                    back = true;
                    break;
            }
        }
    }

    private void adminManualEmailMenu() {
        if (!(p instanceof Admin)) {
            menuDisplay.error("Admin privileges required.");
            return;
        }
        try {
            String memberEmail = inputHandler.readString("Enter member email/username: ");
            Member member = memberService.findMemberByEmail(memberEmail);
            if (member == null) {
                menuDisplay.error("Member not found.");
                return;
            }
            String subject = inputHandler.readString("Enter email subject: ");
            String body = inputHandler.readString("Enter email body: ");
            adminActions.sendEmailToMember((Admin) p, member, subject, body);
            menuDisplay.ok("Email sent to " + member.getUserName());
        } catch (Exception ex) {
            menuDisplay.error(ex.getMessage());
        }
    }

    // ========== Member flows ==========
    private void memberMainLoop() {
        while (p instanceof Member) {
            menuDisplay.showMemberMenu();
            int choice = inputHandler.readIntRange("Select an option: ", 1, 7);
            switch (choice) {
                case 1: // Search book (strategy-based)
                    int searchType = inputHandler.readIntRange(
                            "Search by: 1) Title  2) Author  3) ISBN: ", 1, 3);
                    switch (searchType) {
                        case 1:
                            bookService.getRepository().setSearchStrategy(new BookTitleSearchStrategy());
                            break;
                        case 2:
                            bookService.getRepository().setSearchStrategy(new BookAuthorSearchStrategy());
                            break;
                        case 3:
                            bookService.getRepository().setSearchStrategy(new BookIsbnSearchStrategy());
                            break;
                        default:
                            bookService.getRepository().setSearchStrategy(new BookTitleSearchStrategy());
                    }
                    String keyword = inputHandler.readString("Enter keyword: ");
                    List<Book> results = bookService.search(keyword);
                    menuDisplay.listBooks(results);
                    break;
                case 2: // Borrow book
                    try {
                        String isbn = inputHandler.readString("Enter ISBN to borrow: ");
                        Loan loan = loanService.borrow(isbn, ((Member) p).getUserName());
                        menuDisplay.ok("Borrowed. Due date: " + loan.getDueDate());
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 3: // Return book
                    try {
                        String isbnR = inputHandler.readString("Enter ISBN to return: ");
                        loanService.returnBook(isbnR, ((Member) p).getUserName());
                        menuDisplay.ok("Return processed (if active loan existed).");
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 4: // View my fines
                    menuDisplay.ok("Your fine balance: " + ((Member) p).getFineBalance());
                    break;
                case 5: // View my loans
                    List<Loan> all = loanService.getAllLoans();
                    String my = ((Member) p).getUserName();
                    List<Loan> mine = all.stream()
                            .filter(l -> !l.isReturned() && my.equals(l.getMemberId()))
                            .collect(Collectors.toList());
                    menuDisplay.listLoans("My Active Loans", mine);
                    break;
                case 6: // Pay fine
                    double amount = inputHandler.readDouble("Enter amount to pay: ");
                    try {
                        ((Member) p).payMemberFine(amount);
                        menuDisplay.ok("Payment successful. New balance: " + ((Member) p).getFineBalance());
                    } catch (Exception ex) {
                        menuDisplay.error(ex.getMessage());
                    }
                    break;
                case 7: // Logout
                    Boolean out = memberService.logout(((Member) p).getUserName());
                    if (Boolean.TRUE.equals(out)) {
                        menuDisplay.ok("Member logged out.");
                    } else {
                        menuDisplay.error("Logout failed or already offline.");
                    }
                    p = null;
                    return;
            }
        }
    }

    // Helpers
    private void printAdminStatus(Person admin) {
        System.out.println(admin.getStatus().toString());
    }
}
