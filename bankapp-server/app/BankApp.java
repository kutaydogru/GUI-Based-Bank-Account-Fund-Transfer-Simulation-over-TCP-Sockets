package app;

import dao.AccountDAO;
import dao.UserDAO;
import model.Account;
import model.User;
import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();

        System.out.println("Welcome to the Bank Application!");

        // User registration/login
        System.out.print("Enter your username: ");
        String username = sc.nextLine();

        User user = userDAO.findUserByUsername(username);
        if (user == null) {
            System.out.println("No user found. Registration required.");
            System.out.print("Choose password: ");
            String password = sc.nextLine();
            System.out.print("Enter your full name: ");
            String fullName = sc.nextLine();
            user = new User(username, password, fullName);
            boolean reg = userDAO.addUser(user);
            if (reg) {
                System.out.println("Registration successful.");
            } else {
                System.out.println("Registration failed. Exiting...");
                return;
            }
        } else {
            System.out.println("Welcome back, " + user.getFullName() + "!");
        }

        boolean running = true;
        while (running) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1. List all users");
            System.out.println("2. List my accounts");
            System.out.println("3. Open a new account");
            System.out.println("4. Make a transfer");
            System.out.println("5. Check account balance");
            System.out.println("6. Exit");
            System.out.print("Your choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    userDAO.listAllUsers();
                    break;
                case 2:
                    accountDAO.listAccountsByUsername(user.getUsername());
                    break;
                case 3:
                    System.out.print("Choose your account number (must be unique): ");
                    String accountNo = sc.nextLine();
                    if (accountDAO.accountExists(accountNo)) {
                        System.out.println("This account number already exists!");
                        break;
                    }
                    System.out.print("Initial balance: ");
                    double balance = Double.parseDouble(sc.nextLine());
                    // Full name for account owner comes from User object for consistency
                    Account acc = accountDAO.createAccount(accountNo, balance, user.getFullName(), user.getUsername());
                    if (acc != null) {
                        System.out.println("Account created: " + acc);
                    } else {
                        System.out.println("Failed to create account.");
                    }
                    break;
                case 4:
                    System.out.print("Your account number: ");
                    String from = sc.nextLine();
                    System.out.print("Recipient account number: ");
                    String to = sc.nextLine();

                    // Check sender account existence
                    if (!accountDAO.accountExists(from)) {
                        System.out.println("Your account does not exist! Transfer aborted.");
                        break;
                    }
                    // Check receiver account existence
                    if (!accountDAO.accountExists(to)) {
                        System.out.println("Recipient account does not exist! Transfer aborted.");
                        break;
                    }

                    System.out.print("Amount: ");
                    double amount = Double.parseDouble(sc.nextLine());
                    if (accountDAO.transfer(from, to, amount)) {
                        System.out.println("Transfer successful.");
                    } else {
                        System.out.println("Transfer failed.");
                    }
                    break;
                case 5:
                    System.out.print("Enter account number to check balance: ");
                    String balNo = sc.nextLine();
                    double bal = accountDAO.getBalance(balNo);
                    if (bal >= 0) {
                        System.out.printf("Balance: %.2f%n", bal);
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;
                case 6:
                    running = false;
                    System.out.println("Exit.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }
}