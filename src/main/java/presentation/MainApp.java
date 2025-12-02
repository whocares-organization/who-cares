package presentation;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import application.AdminActions;
import application.AdminFileLoader;
import application.AdminService;
import application.BookService;
import application.EmailService;
import application.LoanService;
import application.LoginService;
import application.MemberService;
import application.OverdueEmailObserver;
import application.ReminderService;
import application.SendRealEmail;
import application.TestingModeLoanWatcher;
import applicationsearchbooks.BookTitleSearchStrategy;
import persistence.AdminRepository;
import persistence.BookRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;


/**
 * Entry point for the Library Management System CLI application.
 *
 * <p>Bootstraps services and launches the interactive menu controller.</p>
 */
public class MainApp {

    /**
     * Utility class; not intended to be instantiated.
     */
    private MainApp() { /* prevent instantiation */ }

    /**
     * Starts the application.
     *
     * @param args command-line arguments (unused)
     * @throws Exception if initialization fails
     */
    public static void main(String[] args) throws Exception {
    	
        // ---------------------------
        // Create repositories
        // ---------------------------
        AdminRepository adminRepo = new AdminRepository();
        MemberRepository memberRepo = new MemberRepository();
        BookRepository bookRepo = new BookRepository();
        LoanRepository loanRepo = new LoanRepository();


        // ---------------------------
        // Configure repository strategies
        // ---------------------------
        bookRepo.setSearchStrategy(new BookTitleSearchStrategy());

        // ---------------------------
        // Create services
        // ---------------------------
        AdminService adminService = new AdminService(adminRepo);
        MemberService memberService = new MemberService(memberRepo);
        BookService bookService = new BookService(bookRepo);
        LoanService loanService = new LoanService(loanRepo);

      
        EmailService emailService;
        try {
           
            emailService = createRealEmailServiceOrFallback();
        } catch (Exception ex) {
            emailService = (to, subject, body) ->
                    System.out.println("[EMAIL] To: " + to + " | Subject: " + subject + " | Body: " + body);
        }
        
        ReminderService reminderService = new ReminderService(loanService, memberService, emailService);
        AdminActions adminActions = new AdminActions(memberService, loanService, emailService);

      
        loanService.addObserver(new OverdueEmailObserver(emailService));

        // ---------------------------
        // Load admins from file
        // ---------------------------
        AdminFileLoader loader = new AdminFileLoader("admins.txt");
        adminService.loadAdmins(loader);

        // ---------------------------
        // Create login service
        // ---------------------------
        LoginService loginService = new LoginService(adminService, memberService);

        // ---------------------------
        // Background scheduler for overdues
        // ---------------------------
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDate today = LocalDate.now();
                loanService.scanAndNotifyOverdues(today);
                reminderService.sendOverdueReminders();
            } catch (Exception e) {
                
                System.err.println("[Scheduler] Error during overdue scan: " + e.getMessage());
            }
        }, 60, 60, TimeUnit.SECONDS); 

        // ---------------------------
        // New: dedicated watcher for testing-mode loans (runs as daemon thread)
        // ---------------------------
        TestingModeLoanWatcher testingWatcher = new TestingModeLoanWatcher(loanService, emailService);
        testingWatcher.start();

        // ---------------------------
        // Presentation Layer
        // ---------------------------
        MenuDisplay menuDisplay = new MenuDisplay();
        InputHandler inputHandler = new InputHandler();

        MenuController menuController = new MenuController(
                menuDisplay,
                inputHandler,
                loginService,
                adminService,
                memberService,
                bookService,
                loanService,
                reminderService,
                adminActions
        );
        

        // ---------------------------
        // Run the CLI application
        // ---------------------------
        menuController.run();

        
        scheduler.shutdownNow();
    }

    private static EmailService createRealEmailServiceOrFallback() {
        try {
            
            io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
            String username = dotenv.get("EMAIL_USERNAME");
            String password = dotenv.get("EMAIL_PASSWORD");
            if (username == null || password == null) {
                throw new IllegalStateException("Missing EMAIL_USERNAME or EMAIL_PASSWORD");
            }
            return new SendRealEmail(username, password);
        } catch (Exception ex) {
           
            throw new RuntimeException(ex);
        }
    }
}