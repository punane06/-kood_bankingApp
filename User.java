package bankingApp.bankingApp;

import java.math.BigDecimal;

public class User {
    private final String name;
    private BigDecimal cash;
    private final SavingsAccount savingAccount;
    private final InvestmentAccount investmentAccount;

    public User(String name) {
        this.name = name;
        this.cash = new BigDecimal("1000.00"); //Initial cash
        this.savingAccount = new SavingsAccount();
        this.investmentAccount = new InvestmentAccount();
    }

    //Getter for all fields
    public String getName() {
        return name;
    }

    public BigDecimal getCash() {
        return cash;    
    }
    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public SavingsAccount getSavingAccount() {
        return savingAccount;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAccount;
    }   
}
