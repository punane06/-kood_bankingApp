package bankingApp.bankingApp.service;

import bankingApp.bankingApp.Fund;
import bankingApp.bankingApp.InvalidAmountException;
import bankingApp.bankingApp.User;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BankingService {
    private final Scanner scanner;
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    public BankingService() {
        this.scanner = new Scanner(System.in);
        // Initialize the four supported users
        initializeUsers();
    }

    private void initializeUsers() {
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        for (String name : names) {
            users.put(name.toLowerCase(), new User(name));
        }
    }

    public void startApp() {
        while (true) {
            if (currentUser == null) {
                login();
            } else {
                if (!displayMenu()) {
                    break;
                }
            }
        }
    }

    private void login() {
        System.out.print("Enter your name to login: ");
        System.out.flush();
        String name = scanner.nextLine().trim();
        if (users.containsKey(name.toLowerCase())) {
            currentUser = users.get(name.toLowerCase());
            System.out.println("Welcome, " + currentUser.getName() + "!");
        } else {
            System.out.println("Error: User not found.");
        }
    }
    private boolean displayMenu() {
        System.out.println();
        System.out.println("--- Banking App Menu ---");
        System.out.println("1. Show balance");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Send money to a person");
        System.out.println("5. Invest in funds");
        System.out.println("6. Transfer between accounts");
        System.out.println("7. Withdraw all investments");
        System.out.println("8. Logout");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
        System.out.flush();
        try {
            String choice = scanner.nextLine();
            return handleMenuChoice(choice);
        } catch (java.util.NoSuchElementException e) {
            return false;
        }
    }

    private boolean handleMenuChoice(String choice) {
        try {
            switch (choice) {
                case "1":
                    showBalance();
                    break;
                case "2":
                    deposit();
                    break;
                case "3":
                    withdraw();
                    break;
                case "4":
                    sendMoney();
                    break;
                case "5":
                    invest();
                    break;
                case "6":
                    transfer();
                    break;
                case "7":
                    currentUser.getInvestmentAccount().withdrawAllInvestements();
                    System.out.println("All investments withdrawn.");
                    break;
                case "8":
                    currentUser = null;
                    System.out.println("Logged out.");
                    break;
                case "9":
                    System.out.println("Thank you for using our banking app. Goodbye!");
                    return false;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private void showBalance() {
        // Requirement: Apply interest/gains whenever balance is viewed
        currentUser.getSavingAccount().applyInterest();
        currentUser.getInvestmentAccount().applyGains();

        System.out.println("Cash: $" + currentUser.getCash());
        System.out.println("Savings: $" + currentUser.getSavingAccount().getBalance());
        System.out.println("Investment Account: $" + currentUser.getInvestmentAccount().getBalance());
        
        // Show detailed fund investments
        Map<Fund, BigDecimal> funds = currentUser.getInvestmentAccount().getInvestedAmounts();
        funds.forEach((fund, amount) -> {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(fund + ": $" + amount);
            }
        });
    }

    private void deposit() throws InvalidAmountException {
        System.out.println("Enter amount to deposit:");
        String input = scanner.nextLine();
        BigDecimal amount = new BigDecimal(input);

        // Kontrollime, et summa ei oleks negatiivne
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive.");
        }

        // Kontrollime, kas kasutajal on piisavalt sularaha
        if (currentUser.getCash().compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient cash.");
        }

        // Teostame tehingu
        currentUser.setCash(currentUser.getCash().subtract(amount));
        BigDecimal currentBalance = currentUser.getSavingAccount().getBalance();
        currentUser.getSavingAccount().setBalance(currentBalance.add(amount));

        System.out.println("Successfully deposited $" + amount);
    }

    private void withdraw() throws InvalidAmountException {
        System.out.println("Enter amount to withdraw:");
        String input = scanner.nextLine();
        BigDecimal amount = new BigDecimal(input);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive.");
        }

        BigDecimal savingsBalance = currentUser.getSavingAccount().getBalance();
        if (savingsBalance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds in savings account.");
        }

        currentUser.getSavingAccount().setBalance(savingsBalance.subtract(amount));
        currentUser.setCash(currentUser.getCash().add(amount));

        System.out.println("Successfully withdrew $" + amount);
    }

    private void sendMoney() throws InvalidAmountException {
        System.out.println("Enter recipient name:");
        String recipientName = scanner.nextLine().trim().toLowerCase();

        if (!users.containsKey(recipientName)) {
            throw new InvalidAmountException("Recipient not found.");
        }

        User recipient = users.get(recipientName);
        
        // Endale ei saa saata
        if (recipient == currentUser) {
            throw new InvalidAmountException("You cannot send money to yourself.");
        }

        System.out.println("Enter amount to send:");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive.");
        }

        // Raha läheb säästukontolt
        BigDecimal mySavings = currentUser.getSavingAccount().getBalance();
        if (mySavings.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds.");
        }

        // Võtame saatjalt
        currentUser.getSavingAccount().setBalance(mySavings.subtract(amount));
        
        // Paneme saajale säästukontole
        BigDecimal recipientSavings = recipient.getSavingAccount().getBalance();
        recipient.getSavingAccount().setBalance(recipientSavings.add(amount));

        System.out.println("Sent $" + amount + " to " + recipient.getName());
    }

    private void transfer() throws InvalidAmountException {
        System.out.println("Select transfer direction:");
        System.out.println("1. Savings to Investment");
        System.out.println("2. Investment to Savings");
        
        String direction = scanner.nextLine();
        System.out.println("Enter amount:");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive.");
        }

        if (direction.equals("1")) {
            // Savings -> Investment
            if (currentUser.getSavingAccount().getBalance().compareTo(amount) < 0) {
                throw new InvalidAmountException("Insufficient funds in Savings.");
            }
            currentUser.getSavingAccount().setBalance(currentUser.getSavingAccount().getBalance().subtract(amount));
            currentUser.getInvestmentAccount().setBalance(currentUser.getInvestmentAccount().getBalance().add(amount));
        } else if (direction.equals("2")) {
            // Investment -> Savings
            if (currentUser.getInvestmentAccount().getBalance().compareTo(amount) < 0) {
                throw new InvalidAmountException("Insufficient funds in Investment account.");
            }
            currentUser.getInvestmentAccount().setBalance(currentUser.getInvestmentAccount().getBalance().subtract(amount));
            currentUser.getSavingAccount().setBalance(currentUser.getSavingAccount().getBalance().add(amount));
        } else {
            System.out.println("Invalid direction.");
            return;
        }
        System.out.println("Transfer successful.");
    }

    private void invest() throws InvalidAmountException {
        System.out.println("Select fund:");
        System.out.println("1. LOW_RISK (2%)");
        System.out.println("2. MEDIUM_RISK (5%)");
        System.out.println("3. HIGH_RISK (10%)");

        String fundChoice = scanner.nextLine();
        Fund selectedFund;

        switch (fundChoice) {
            case "1": selectedFund = Fund.LOW_RISK; break;
            case "2": selectedFund = Fund.MEDIUM_RISK; break;
            case "3": selectedFund = Fund.HIGH_RISK; break;
            default:
                System.out.println("Invalid fund choice.");
                return;
        }

        System.out.println("Enter amount to invest from your investment account:");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive.");
        }

        // Kontrollime, kas investeerimiskontol on vaba raha (mitte juba teistes fondides olevat raha)
        BigDecimal investmentBalance = currentUser.getInvestmentAccount().getBalance();
        if (investmentBalance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds in Investment account balance.");
        }

        // Vähendame vaba jääki ja suurendame fondi seisu
        currentUser.getInvestmentAccount().setBalance(investmentBalance.subtract(amount));
        
        Map<Fund, BigDecimal> investedMap = currentUser.getInvestmentAccount().getInvestedAmounts();
        BigDecimal currentInFund = investedMap.get(selectedFund);
        investedMap.put(selectedFund, currentInFund.add(amount));

        System.out.println("Successfully invested $" + amount + " into " + selectedFund);
    }
}
