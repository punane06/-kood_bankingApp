package bankingApp.bankingApp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InvestmentAccount extends Account {
    //Stores how much money is in each specific fund
    private final Map<Fund, BigDecimal> investedAmounts = new HashMap<>();

    public InvestmentAccount() {
        super();
        //Initialize funds with zero
        for (Fund fund : Fund.values()) {
            investedAmounts.put(fund, BigDecimal.ZERO);
        }
    }

    public void applyGains() {
        for (Fund fund : Fund.values()) {
            BigDecimal currentAmount = investedAmounts.get(fund);
            if (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal gain = currentAmount.multiply(fund.getRate());
                investedAmounts.put(fund, currentAmount.add(gain));
            }
        }
    }

    public Map<Fund, BigDecimal> getInvestedAmounts() {
        return investedAmounts;
    }

    public void withdrawAllInvestements() {
        for (Fund fund : Fund.values()) {
            balance = balance.add(investedAmounts.get(fund));
            investedAmounts.put(fund, BigDecimal.ZERO);
        }
    }
}
