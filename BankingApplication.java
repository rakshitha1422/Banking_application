import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

class User {
    private String username;
    private String password;
    private ArrayList<Account> accounts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }
}

class Account {
    private static int nextAccountNumber = 1000;
    private int accountNumber;
    private String accountHolderName;
    private String accountType;
    private double balance;
    private ArrayList<Transaction> transactions;

    public Account(String accountHolderName, String accountType, double initialDeposit) {
        this.accountNumber = nextAccountNumber++;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        logTransaction("Initial Deposit", initialDeposit);
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        logTransaction("Deposit", amount);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        logTransaction("Withdrawal", amount);
        return true;
    }

    public void logTransaction(String type, double amount) {
        transactions.add(new Transaction(type, amount));
    }

    public void addMonthlyInterest(double interestRate) {
        if (accountType.equalsIgnoreCase("savings")) {
            double interest = balance * interestRate;
            deposit(interest);
            logTransaction("Interest", interest);
        }
    }

    public void printStatement() {
        System.out.println("Statement for Account: " + accountNumber);
        System.out.println("Date\t\t\tType\t\tAmount");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }
}

class Transaction {
    private String transactionId;
    private Date date;
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.transactionId = UUID.randomUUID().toString();
        this.date = new Date();
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return date + "\t" + type + "\t" + amount;
    }
}

public class BankingApplication {
    private static ArrayList<User> users = new ArrayList<>();
    private static User currentUser;
    private static final double INTEREST_RATE = 0.02; // 2% monthly interest for savings accounts
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to the Banking Application");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    if (login()) {
                        mainMenu();
                    }
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void register() {
        System.out.println("Enter a username:");
        String username = scanner.nextLine();
        System.out.println("Enter a password:");
        String password = scanner.nextLine();

        users.add(new User(username, password));
        System.out.println("Registration successful!");
    }

    private static boolean login() {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.authenticate(password)) {
                currentUser = user;
                System.out.println("Login successful!");
                return true;
            }
        }
        System.out.println("Invalid credentials, please try again.");
        return false;
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\nBanking Menu");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. View Statement");
            System.out.println("5. Check Balance");
            System.out.println("6. Add Monthly Interest (Savings)");
            System.out.println("7. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    openAccount();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    viewStatement();
                    break;
                case 5:
                    checkBalance();
                    break;
                case 6:
                    addMonthlyInterest();
                    break;
                case 7:
                    currentUser = null;
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void openAccount() {
        System.out.println("Enter account holder's name:");
        String accountHolderName = scanner.nextLine();
        System.out.println("Enter account type (savings/checking):");
        String accountType = scanner.nextLine();
        System.out.println("Enter initial deposit amount:");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        Account account = new Account(accountHolderName, accountType, initialDeposit);
        currentUser.addAccount(account);
        System.out.println("Account created successfully. Account Number: " + account.getAccountNumber());
    }

    private static void deposit() {
        Account account = selectAccount();
        if (account == null) return;

        System.out.println("Enter deposit amount:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        account.deposit(amount);
        System.out.println("Deposit successful!");
    }

    private static void withdraw() {
        Account account = selectAccount();
        if (account == null) return;

        System.out.println("Enter withdrawal amount:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        if (account.withdraw(amount)) {
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    private static void viewStatement() {
        Account account = selectAccount();
        if (account == null) return;

        account.printStatement();
    }

    private static void checkBalance() {
        Account account = selectAccount();
        if (account == null) return;

        System.out.println("Current balance: $" + account.getBalance());
    }

    private static void addMonthlyInterest() {
        for (Account account : currentUser.getAccounts()) {
            account.addMonthlyInterest(INTEREST_RATE);
        }
        System.out.println("Monthly interest added to savings accounts.");
    }

    private static Account selectAccount() {
        System.out.println("Enter account number:");
        int accountNumber = scanner.nextInt();
        scanner.nextLine(); // consume newline

        for (Account account : currentUser.getAccounts()) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }
        System.out.println("Account not found.");
        return null;
    }
}
