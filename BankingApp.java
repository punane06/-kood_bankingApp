package bankingApp.bankingApp;

import bankingApp.bankingApp.service.BankingService;

public class BankingApp {
    public static void main(String[] args) {
        // Instantiate only one service, which contains one scanner
        BankingService service = new BankingService();
        service.startApp();
    }
}
