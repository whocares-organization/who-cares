package domain;

import java.util.UUID;

/**
 * Represents a library member who can borrow books and manage fines.
 *
 * <p>This class stores the member’s basic information including their
 * unique ID, name, and current fine balance. It provides methods to
 * add fines, pay fines, and determine whether the member is eligible
 * to borrow books.</p>
 */
public class Member {

    /** A unique identifier automatically assigned to each member. */
    private String memberId;

    /** The full name of the member. */
    private String name;

    /** The total outstanding fine balance for the member. */
    private double fineBalance;

    /**
     * Constructs a new {@code Member} with the specified name.
     * A unique ID is automatically generated and the fine balance is initialized to zero.
     *
     * @param name the name of the member
     */
    public Member(String name) {
        this.memberId = UUID.randomUUID().toString();
        this.name = name;
        this.fineBalance = 0.0;
    }

    /**
     * Returns the unique ID of the member.
     *
     * @return the member’s ID
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Returns the name of the member.
     *
     * @return the member’s name
     */
    public String getName() {
        return name;
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
     * Adds a fine amount to the member’s account.
     *
     * <p>This method increases the fine balance by the specified amount.
     * It ignores negative or zero amounts.</p>
     *
     * @param amount the amount to add as a fine
     */
    public void addFine(double amount) {
        if (amount > 0) {
            fineBalance += amount;
            System.out.println("Fine of " + amount + " added. Total fine: " + fineBalance);
        }
    }

    /**
     * Pays part or all of the member’s fine.
     *
     * <p>If the payment amount exceeds the fine balance, the fine is cleared
     * and a message is displayed. Negative or zero payments are ignored.</p>
     *
     * @param amount the amount to pay toward the fine
     */
    public void payFine(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid payment amount.");
            return;
        }

        if (amount <= fineBalance) {
            fineBalance -= amount;
            System.out.println("Paid " + amount + ". Remaining fine: " + fineBalance);
        } else {
            System.out.println("Payment exceeds fine balance. Full fine paid.");
            fineBalance = 0;
        }
    }

    /**
     * Determines whether the member is eligible to borrow books.
     *
     * <p>A member can borrow books only if their fine balance is zero.</p>
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
                "memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", fineBalance=" + fineBalance +
                '}';
    }
}
