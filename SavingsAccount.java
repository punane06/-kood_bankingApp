package bankingApp.model;

import java.math.BigDecimal;

public class SavingsAccount extends Account {
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.01");

    public void applyInterest() {
        //formula : balance = balance + (balance * 0.01)
        BigDecimal interest = balance.multiply(INTEREST_RATE);
        balance = balance.add(interest);
    }
}
