import java.util.ArrayList;
import java.util.Scanner;

// Interface untuk transaksi
interface Transaction {
    void execute();
}

// Class untuk menyimpan informasi transaksi
class TransactionHistory {
    private ArrayList<Transaction> transactions;

    public TransactionHistory() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void showTransactionHistory() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction.toString());
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}

// Class dasar untuk pengguna
class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }
}

// Class untuk nasabah
class Customer extends User {
    private Account account;

    public Customer(String username, String password) {
        super(username, password);
        this.account = new Account();
    }

    public Account getAccount() {
        return account;
    }
}

// Class untuk admin
class Admin extends User {
    public Admin(String username, String password) {
        super(username, password);
    }

    public void viewAllCustomers(Bank bank) {
        ArrayList<Customer> customers = bank.getCustomers();
        System.out.println("=== Semua Pengguna ===");
        for (Customer customer : customers) {
            System.out.println("Username: " + customer.getUsername());
        }
    }
}

// Class untuk transaksi simpan uang
class Deposit implements Transaction {
    private Customer customer;
    private double amount;

    public Deposit(Customer customer, double amount) {
        this.customer = customer;
        this.amount = amount;
    }

    @Override
    public void execute() {
        customer.getAccount().deposit(amount);
        customer.getAccount().getTransactionHistory().addTransaction(this);
        System.out.println("Deposit: " + amount + " Ke " + customer.getUsername());
    }

    @Override
    public String toString() {
        return "Deposit: " + amount + " Dari " + customer.getUsername();
    }
}

// Class untuk transaksi transfer uang
class Transfer implements Transaction {
    private Customer sourceCustomer;
    private Customer destinationCustomer;
    private double amount;

    public Transfer(Customer sourceCustomer, Customer destinationCustomer, double amount) {
        this.sourceCustomer = sourceCustomer;
        this.destinationCustomer = destinationCustomer;
        this.amount = amount;
    }

    @Override
    public void execute() {
        sourceCustomer.getAccount().withdraw(amount);
        destinationCustomer.getAccount().deposit(amount);
        sourceCustomer.getAccount().getTransactionHistory().addTransaction(this);
        destinationCustomer.getAccount().getTransactionHistory().addTransaction(this);
        System.out.println("Transfer: " + amount + " dari " + sourceCustomer.getUsername() + " ke " + destinationCustomer.getUsername());
    }

    @Override
    public String toString() {
        return "Transfer: " + amount + " dari " + sourceCustomer.getUsername() + " ke " + destinationCustomer.getUsername();
    }

    // Getter method for destinationCustomer
    public Customer getDestinationCustomer() {
        return destinationCustomer;
    }
}

// Class untuk menyimpan informasi akun
class Account {
    private double balance;
    private TransactionHistory transactionHistory;

    public Account() {
        this.balance = 0.0;
        this.transactionHistory = new TransactionHistory();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
        } else {
            System.out.println("SALDO TIDAK CUKUP!");
        }
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }
}

// Class untuk menyimpan informasi bank dan mengatur nasabah
class Bank {
    private ArrayList<Customer> customers;

    public Bank() {
        this.customers = new ArrayList<>();
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
}

// Class untuk aplikasi konsol
public class UAS {
    private static Bank bank = new Bank();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bank bank = new Bank();

        // Inisialisasi beberapa nasabah dan admin
        Customer customer1 = new Customer("Erik", "123");
        Customer customer2 = new Customer("Eris", "321");
        Admin admin = new Admin("admin", "adminis");

        bank.addCustomer(customer1);
        bank.addCustomer(customer2);

        while (true) {
            System.out.println("\n=== Bank System Menu ===");
            System.out.println("1. User Login");
            System.out.println("2. Admin Login");
            System.out.println("3. Keluar");
            System.out.println("========================");

            System.out.println("Masukan Pilihan:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    userLogin(bank);
                    break;
                case 2:
                    adminLogin(admin, bank);
                    break;
                case 3:
                    System.out.println("...SHUTTING DOWN...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Tidak ada pilihan tersebut, coba lagi!");
            }
        }
    }

    private static void userLogin(Bank bank) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Masukan username:");
        String username = scanner.nextLine();
        System.out.println("Masukan password:");
        String password = scanner.nextLine();

        for (Customer customer : bank.getCustomers()) {
            if (customer.getUsername().equals(username) && customer.authenticate(password)) {
                performUserActions(customer);
                return;
            }
        }

        System.out.println("Username atau password salah.");
    }

    private static void adminLogin(Admin admin, Bank bank) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Masukan admin username:");
        String username = scanner.nextLine();
        System.out.println("Masukan admin password:");
        String password = scanner.nextLine();

        if (admin.getUsername().equals(username) && admin.authenticate(password)) {
            performAdminActions(admin, bank);
        } else {
            System.out.println("Username atau password salah.");
        }
    }

    private static void performUserActions(Customer customer) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Daftar Pilihan ===");
            System.out.println("1. Deposit");
            System.out.println("2. Transfer");
            System.out.println("3. Lihat Transaksi");
            System.out.println("4. Logout");
            System.out.println("=====================");

            System.out.println("Masukan pilihan:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.println("Masukan jumlah deposit:");
                    double depositAmount = scanner.nextDouble();
                    new Deposit(customer, depositAmount).execute();
                    break;
                case 2:
				System.out.println("Masukan tujuan username untuk transfer:");
				String destinationUsername = scanner.nextLine();
				Customer destinationCustomer = findCustomerByUsername(destinationUsername);
				if (destinationCustomer != null) {
					System.out.println("Masukan jumlah transfer :");
					double transferAmount = scanner.nextDouble();
					// Properly set destinationCustomer when creating Transfer transaction
					new Transfer(customer, destinationCustomer, transferAmount).execute();
				} else {
					System.out.println("Tujuan pengguna tidak ditemukan.");
				}
    break;
                case 3:
                    customer.getAccount().getTransactionHistory().showTransactionHistory();
                    break;
                case 4:
                    System.out.println("Keluar.");
                    return;
                default:
                    System.out.println("Tidak ada pilihan tersebut, coba lagi!");
            }
        }
    }

    private static void performAdminActions(Admin admin, Bank bank) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Admin System ===");
            System.out.println("1. Lihat semua Customers");
            System.out.println("2. Logout");
            System.out.println("=====================");

            System.out.println("Masukan pilihan:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    admin.viewAllCustomers(bank);
                    break;
                case 2:
                    System.out.println("Logging out.");
                    return;
                default:
                    System.out.println("Tidak ada pilihan tersebut, coba lagi!");
            }
        }
    }

    private static Customer findCustomerByUsername(String username) {
    for (Customer customer : bank.getCustomers()) {
        if (customer.getUsername().equals(username)) {
            return customer;
        }
    }
    return null;
}
}
