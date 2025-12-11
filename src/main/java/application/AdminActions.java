package application;

import java.time.LocalDate;
import java.util.logging.Logger;

import domain.Admin;
import domain.UserStatus;
import domain.Member;
import domain.Person;
import domain.Media;
import domain.Loan;
import persistence.AdminRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

/**
 * Application-level façade for administrator operations.
 * Delegates to domain services instead of talking directly to repositories when possible.
 */
public class AdminActions {

    private static final Logger LOGGER = Logger.getLogger(AdminActions.class.getName());

    private final MemberService memberService;
    private final LoanService loanService;
    private final EmailService emailService;

    // =========================
    // Constructors
    // =========================

    /**
     * Creates an instance with default repositories wired into services.
     */
    public AdminActions() {
        this(
            new MemberService(new MemberRepository()),
            new LoanService(new BorrowingRules(), new LoanRepository()),
            null
        );
    }

    /**
     * Creates an instance with explicit services (no email service).
     *
     * @param memberService the member service to use
     * @param loanService the loan service to use
     */
    public AdminActions(MemberService memberService, LoanService loanService) {
        this(memberService, loanService, null);
    }

    /**
     * Creates an instance with explicit services and optional email service.
     *
     * @param memberService the member service to use
     * @param loanService the loan service to use
     * @param emailService the email service (may be {@code null})
     */
    public AdminActions(MemberService memberService, LoanService loanService, EmailService emailService) {
        this.memberService = memberService;
        this.loanService = loanService;
        this.emailService = emailService;
    }

    // =========================
    // Admin validation
    // =========================

    /**
     * Indicates whether the provided admin exists.
     *
     * @param admin the admin to check
     * @return {@code true} if the admin exists; {@code false} otherwise
     */
    public boolean isAdmin(Admin admin) {
        if (admin == null || admin.getUserName() == null) return false;
        return AdminRepository.findAdminByEmail(admin.getUserName()) != null;
    }

    private boolean hasActiveSession(Admin admin) {
        return isAdmin(admin) && admin.getStatus() == UserStatus.ONLINE;
    }

    // =========================
    // Member management
    // =========================

    /**
     * Registers a new member. Only valid online admins may register.
     *
     * @param admin the admin performing the registration
     * @param newMember the member to register
     * @throws IllegalStateException if admin is not logged in or registration fails
     */
    public void registerMember(Admin admin, Member newMember) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to register members.");
        }
        Boolean result = memberService.registerMember(newMember);
        if (result == null || !result) {
            throw new IllegalStateException("Failed to register member (maybe already exists or invalid).");
        }
        LOGGER.info("Member registered successfully: " + newMember.getUserName());
    }

    /**
     * Unregisters a member if eligible.
     *
     * @param admin the admin performing the action
     * @param memberId the member identifier (ID)
     * @throws IllegalStateException if admin is not logged in or member cannot be unregistered
     * @throws IllegalArgumentException if the member does not exist
     */
    public void unregisterMember(Admin admin, String memberId) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to unregister members.");
        }
        Member member = memberService.findMemberById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found.");
        }
        if (!canBeUnregistered(member)) {
            throw new IllegalStateException("Cannot unregister member with active loans or unpaid fines.");
        }
        Boolean removed = memberService.unregisterMember(memberId);
        if (removed == null || !removed) {
            throw new IllegalStateException("Failed to unregister member.");
        }
        LOGGER.info("Member unregistered successfully: " + memberId);
    }
    
    /**
     * Indicates whether a member can be unregistered.
     *
     * @param member the member to check
     * @return {@code true} if the member has no active loans and no fines
     */
    public boolean canBeUnregistered(Member member) {
        if (member == null) return false;
        boolean noActiveLoans = !loanService.hasActiveLoans(member.getUserName());
        boolean noFines = member.getFineBalance() <= 0.0;
        return noActiveLoans && noFines;
    }

    // =========================
    // Polymorphic media borrowing
    // =========================

    /**
     * Borrows media for a member using the loan service.
     *
     * @param admin the admin performing the action
     * @param member the borrowing member
     * @param media the media to borrow
     * @param borrowDate the borrow date
     * @return the created loan instance
     * @throws IllegalStateException if admin is not logged in
     * @throws IllegalArgumentException if member or media is {@code null}
     */
    public Loan borrowMedia(Admin admin, Member member, Media media, LocalDate borrowDate) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to borrow media.");
        }
        if (member == null || media == null) {
            throw new IllegalArgumentException("Member and media must be provided.");
        }
        return loanService.borrowMedia(member, media, borrowDate);
    }

    /**
     * Returns media associated with a loan.
     *
     * @param admin the admin performing the action
     * @param loan the loan to return
     * @param returnDate the return date
     * @throws IllegalStateException if admin is not logged in
     * @throws IllegalArgumentException if the loan is {@code null}
     */
    public void returnMedia(Admin admin, Loan loan, LocalDate returnDate) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to return media.");
        }
        if (loan == null) {
            throw new IllegalArgumentException("Loan must be provided.");
        }
        loanService.returnMedia(loan, returnDate);
    }

    // =========================
    // Fine summary
    // =========================

    /**
     * Calculates total fines for a member as of the given date.
     *
     * @param admin the admin performing the action
     * @param member the member to evaluate
     * @param today the current date used for calculation
     * @return the total fines associated with the member's loans
     * @throws IllegalStateException if admin is not logged in
     */
    public double calculateMemberFineSummary(Admin admin, Member member, LocalDate today) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to view fine summaries.");
        }
        if (member == null) return 0.0;
        return loanService.calculateTotalFinesForMember(member, today);
    }

    private String resolveMemberIdentifier(Member member) {
        return member.getUserName();
    }

    /**
     * Searches for a member by ID if the admin is online.
     *
     * @param p the admin performing the search
     * @param memberIdToSearch the member ID to search for
     * @return the person or {@code null} if not found or not authorized
     */
    public Person searchMemberById(Admin p, String memberIdToSearch) {
      
        if (!hasActiveSession(p) || memberIdToSearch == null) {
            return null;
        }

        
        try {
    return memberService.findMemberById(memberIdToSearch);
        } catch (NullPointerException ex) {
            LOGGER.warning("Member lookup threw NullPointerException: " + ex.getMessage());
        }

        return null;
    }


    /**
     * Sends an email to a member using the configured email service.
     *
     * @param admin the admin performing the action
     * @param member the target member
     * @param subject the subject line
     * @param body the email body
     * @throws IllegalStateException if admin is not logged in or email service is not configured
     * @throws IllegalArgumentException if the member is invalid or has an empty username
     */
    public void sendEmailToMember(Admin admin, Member member, String subject, String body) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to send emails.");
        }
        if (emailService == null) {
            throw new IllegalStateException("Email service is not configured.");
        }
        if (member == null || member.getUserName() == null || member.getUserName().isBlank()) {
            throw new IllegalArgumentException("Valid member is required.");
        }
        emailService.sendEmail(member.getUserName(), subject, body);
    }

    /**
     * Testing-only polymorphic borrow method with advanced custom duration.
     *
     * @param admin the admin performing the action
     * @param member the borrowing member
     * @param media the media item
     * @param borrowDate the borrow date
     * @param days days (>= 0)
     * @param hours hours (>= 0)
     * @param minutes minutes (>= 0)
     * @param seconds seconds (>= 0)
     * @return the created loan
     * @throws IllegalStateException if admin is not logged in
     * @throws IllegalArgumentException if member/media are invalid or duration is negative/all zero
     */
    public Loan borrowMediaTestDuration(Admin admin,
                                        Member member,
                                        Media media,
                                        LocalDate borrowDate,
                                        int days,
                                        int hours,
                                        int minutes,
                                        int seconds) {
        if (!hasActiveSession(admin)) {
            throw new IllegalStateException("Admin must be logged in to borrow media in testing mode.");
        }
        if (member == null || media == null) {
            throw new IllegalArgumentException("Member and media must be provided.");
        }
        return loanService.borrowMediaTestDuration(member, media, borrowDate, days, hours, minutes, seconds);
    }

    /**
     * Sends a real email using the configured email service.
     *
     * @param admin the admin performing the action
     * @param member the target member
     * @param subject the subject line
     * @param body the email body
     * @throws IllegalStateException if admin is not logged in or email service is not configured
     * @throws IllegalArgumentException if the member is invalid
     */
    public void sendRealEmailToMember(Admin admin, Member member, String subject, String body) {
        sendEmailToMember(admin, member, subject, body);
    }
}
