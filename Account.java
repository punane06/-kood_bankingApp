package bankingApp.model;

import java.math.BigDecimal;

public abstract class Account {
    protected BigDecimal balance;

    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    
    }
}
