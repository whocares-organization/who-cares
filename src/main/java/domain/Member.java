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
    public Member(String id, String name, String password) {
        super(id, name, password);
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

    /**
     * Adds a fine amount to the member's account.
     *
     * @param amount the amount to add to the fine balance
     */
    public void addFine(double amount) {
        fineBalance += amount;
    }

    /**
     * Pays part or all of the member's fine balance.
     *
     * <p>If the payment amount exceeds the current fine balance, an appropriate
     * message is displayed and the balance remains unchanged.</p>
     *
     * @param amount the amount to pay toward the fine
     */
    public void payFine(double amount) {
        if (amount <= fineBalance) {
            fineBalance -= amount;
            System.out.println("Paid " + amount + ". Remaining fine: " + fineBalance);
        } else {
            System.out.println("Amount exceeds fine balance.");
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
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fineBalance=" + fineBalance +
                '}';
    }
}
