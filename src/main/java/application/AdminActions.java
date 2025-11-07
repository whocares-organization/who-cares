package application;

import java.time.LocalDate;
import java.util.logging.Logger;

import domain.Admin;
import domain.AdminStatus;
import domain.Member;
import domain.Media;
import domain.Loan;
import persistence.AdminRepository;
import persistence.LoanRepository;
import persistence.MemberRepository;

public class AdminActions {
	
	 private static final Logger LOGGER = Logger.getLogger(AdminActions.class.getName());

	 
	 /**
	     * Checks if the provided admin exists in AdminRepository.
	     */
	 public boolean isAdmin(Admin admin) {
		    if (admin == null || admin.getUserName() == null) return false;
		    return AdminRepository.findAdminByEmail(admin.getUserName()) != null;
		}

	 private boolean hasActiveSession(Admin admin) {
	        return isAdmin(admin) && admin.getStatus() == AdminStatus.ONLINE;
	    }
	 
	 /**
	     * Registers a new member. Only valid admins can register.
	     */
	    public void registerMember(Admin admin, Member newMember) {
	        if (!hasActiveSession(admin)) {
	            throw new IllegalStateException("Admin must be logged in to register members.");
	        }
	        MemberRepository.addMember(newMember);
	        LOGGER.info("Member registered successfully: " + newMember.getUserName());
	    }
	    
	    /**
	     * Unregisters a member if conditions are met. Only valid admins can unregister.
	     */
	    public void unregisterMember(Admin admin, String memberId) {
	        if (!hasActiveSession(admin)) {
	            throw new IllegalStateException("Admin must be logged in to unregister members.");
	        }

	        Member member = MemberRepository.findById(memberId);
	        if (member == null) {
	            throw new IllegalArgumentException("Member not found.");
	        }

	        // Check for active loans or unpaid fines
	        if (!canBeUnregistered(member)) {
	            throw new IllegalStateException("Cannot unregister member with active loans or unpaid fines.");
	        }

	        MemberRepository.removeMember(member);
	        LOGGER.info("Member unregistered successfully: " + memberId);
	    }
	    
	    /**
	     * Helper method to check if a member is eligible for deletion.
	     */
	    public boolean canBeUnregistered(Member member) {
	        if (member == null) return false;
	        boolean noActiveLoans = LoanRepository.findActiveByMember(member.getUserName()).isEmpty();
	        boolean noFines = member.getFineBalance() <= 0.0;
	        return noActiveLoans && noFines;
	    }
	    
	    // New polymorphic borrow method
	    public Loan borrowMedia(Admin admin, Member member, Media media, LocalDate borrowDate) {
	        if (!hasActiveSession(admin)) {
	            throw new IllegalStateException("Admin must be logged in to borrow media.");
	        }
	        if (member == null || media == null) {
	            throw new IllegalArgumentException("Member and media must be provided.");
	        }
	        // Enforce borrowing rules (fines + overdue loans)
	        new BorrowingRules().ensureCanBorrow(member, new LoanRepository());
	        if (media.isBorrowed()) {
	            throw new IllegalStateException("Media already borrowed.");
	        }
	        Loan loan = new Loan(media, resolveMemberIdentifier(member), borrowDate);
	        LoanRepository.save(loan);
	        Logger.getLogger(AdminActions.class.getName())
	              .info("Media borrowed: " + media.getId() + " by member " + resolveMemberIdentifier(member));
	        return loan;
	    }

	    // Return media and mark loan returned, clearing media state
	    public void returnMedia(Admin admin, Loan loan, LocalDate returnDate) {
	        if (!hasActiveSession(admin)) {
	            throw new IllegalStateException("Admin must be logged in to return media.");
	        }
	        if (loan == null) {
	            throw new IllegalArgumentException("Loan must be provided.");
	        }
	        if (loan.isReturned()) {
	            throw new IllegalStateException("Loan already returned.");
	        }
	        loan.setReturned(true);
	        Media media = loan.getMedia();
	        if (media != null) {
	            media.markReturned();
	        }
	        Logger.getLogger(AdminActions.class.getName())
	              .info("Media returned: " + loan.getIsbn());
	    }

	    // Fine summary across all loans for member
	    public double calculateMemberFineSummary(Admin admin, Member member, LocalDate today) {
	        if (!hasActiveSession(admin)) {
	            throw new IllegalStateException("Admin must be logged in to view fine summaries.");
	        }
	        if (member == null) return 0.0;
	        double total = 0.0;
	        for (Loan loan : LoanRepository.findAll()) {
	            if (loan.getMemberId() != null && loan.getMemberId().equals(resolveMemberIdentifier(member))) {
	                loan.calculateFine(today);
	                total += loan.getFineAmount();
	            }
	        }
	        return total;
	    }

	    private String resolveMemberIdentifier(Member member) {
	        // Preserve earlier behavior: repositories key by username/email
	        return member.getUserName();
	    }
}