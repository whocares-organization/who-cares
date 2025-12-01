package application;

import domain.Loan;

/**
 * Observer contract for receiving notifications when a loan becomes overdue.
 */
public interface OverdueObserver {
    /**
     * Called when a specific active loan transitions to overdue.
     *
     * @param loan the overdue loan (not returned, past due date)
     */
    void onLoanOverdue(Loan loan);
}
