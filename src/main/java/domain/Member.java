package domain;

/**
 * Represents a library member who can borrow books and manage fines.
 *
 * <p>The {@code Member} class extends {@link Person} and adds functionality
 * specific to library members, including fine management and borrowing eligibility.</p>
 */
public class Member extends Person {

    /** The total fine balance owed by the member. */
    private double fineBalance;

    /**
     * Constructs a new {@code Member} with the specified ID, name, and password.
     * The fine balance is initialized to zero.
     *
     * @param id the unique ID of the member
     * @param name the member's full name
     * @param password the password used for authentication
     */
    public Member(String userName, String password) {
        super(userName, password);
        this.fineBalance = 0.0;
    }
    
    public Member(String id, String userName, String password) {
        super(id, userName, password);
        this.fineBalance = 0.0;
    }
    
    /** Default Constructor. */
    public Member() {
    	
    }
    
    public Member(String userName, String password, String name, String id, String phone) {
        super(userName, password, name, id, phone);
        this.fineBalance = 0.0;
     }

    /**
     * Returns the current fine balance of the member.
     *
     * @return the fine balance
     */
    public double getFineBalance() {
        return fineBalance;
    }

    public void setFineBalance(double fineBalance) {
		this.fineBalance = fineBalance;
	}

    /**
     * Adds a fine amount to the member's account.
     *
     * @param amount the amount to add to the fine balance
     */
	public void addMemberFine(double amount) { 
		fineBalance = fineBalance + amount;
	}
    
	/**
     * Pays part or all of the member's outstanding fines.
     *
     * <p>If the payment amount exceeds the current fine balance,
     * only the remaining balance is cleared and any extra amount is ignored.</p>
     *
     * @param amount the amount to pay toward the fine (must be positive)
     * @throws IllegalArgumentException if the amount is non-positive
     */
	public void payMemberFine(double amount) {
	    if (amount <= 0) {
	        throw new IllegalArgumentException("Payment amount must be positive.");
	    }

	    double currentBalance = fineBalance;

	    if (amount >= currentBalance) {
	    	fineBalance = 0.0;
	    } else {
	    	fineBalance = currentBalance - amount;
	    }
	}
    
    /**
     * Determines whether the member is eligible to borrow books.
     *
     * <p>A member can only borrow books if their fine balance is zero.</p>
     *
     * @return {@code true} if the member can borrow books, {@code false} otherwise
     */
    public boolean canBorrow() {
        return fineBalance == 0;
    }

    /**
     * Returns a string representation of the member with their details.
     *
     * @return a string describing the member
     */
    @Override
    public String toString() {
        return "Member{" +
                ", name='" + this.getName() + '\'' +
                ", fineBalance=" + fineBalance +
                '}';
    }
    
    /**
     * Calculates the total fines across the provided loans for this member as of the given date.
     * Only considers this member's non-returned loans; each loan's fine is recalculated based on today.
     */
    public double calculateTotalFines(Iterable<Loan> loans, java.time.LocalDate today) {
        if (loans == null) return 0.0;
        double total = 0.0;
        String myId = getId();
        for (Loan loan : loans) {
            if (loan != null && !loan.isReturned() && myId != null && myId.equals(loan.getMemberId())) {
                loan.calculateFine(today);
                total += loan.getFineAmount();
            }
        }
        return total;
    }
}