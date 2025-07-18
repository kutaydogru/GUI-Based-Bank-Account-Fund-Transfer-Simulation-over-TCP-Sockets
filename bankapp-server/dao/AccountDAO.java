package dao;

import model.Account;
import java.sql.*;

/**
 * DAO for account operations.
 */
public class AccountDAO {
    private String url = "jdbc:sqlite:db/bank.db";

    // Create a new account (account number is user-chosen and must be unique)
    public Account createAccount(String accountNo, double initialBalance, String fullName, String username) {
        String sql = "INSERT INTO accounts(account_no, balance, full_name, username) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            stmt.setDouble(2, initialBalance);
            stmt.setString(3, fullName);
            stmt.setString(4, username);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return new Account(accountNo, initialBalance, fullName, username);
            }
        } catch (SQLException e) {
            System.out.println("Failed to create account: " + e.getMessage());
        }
        return null;
    }

    // Add a new account (for testing purposes)
    public boolean addAccount(Account account) {
        String sql = "INSERT INTO accounts(account_no, balance, full_name, username) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountNo());
            stmt.setDouble(2, account.getBalance());
            stmt.setString(3, account.getFullName());
            stmt.setString(4, account.getUsername());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add account: " + e.getMessage());
            return false;
        }
    }

    // List all accounts for a given username
    public void listAccountsByUsername(String username) {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            System.out.printf("\nAccounts for user '%s':%n", username);
            while (rs.next()) {
                System.out.printf("- AccountNo: %s | Balance: %.2f | Owner: %s%n",
                        rs.getString("account_no"),
                        rs.getDouble("balance"),
                        rs.getString("full_name"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list accounts: " + e.getMessage());
        }
    }

    // Get balance for an account
    public double getBalance(String accountNo) {
        String sql = "SELECT balance FROM accounts WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get balance: " + e.getMessage());
        }
        return -1;
    }

    // Money transfer: sender and receiver are both account numbers
    public boolean transfer(String fromAccountNo, String toAccountNo, double amount) {
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE account_no = ? AND balance >= ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql)) {
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, fromAccountNo);
                withdrawStmt.setDouble(3, amount);
                int affectedRows = withdrawStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    System.out.println("Insufficient funds or invalid sender account.");
                    return false;
                }
            }

            try (PreparedStatement depositStmt = conn.prepareStatement(depositSql)) {
                depositStmt.setDouble(1, amount);
                depositStmt.setString(2, toAccountNo);
                int affectedRows = depositStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    System.out.println("Invalid recipient account.");
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            return false;
        }
    }

    // Check if an account exists (for error checking)
    public boolean accountExists(String accountNo) {
        String sql = "SELECT 1 FROM accounts WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            return stmt.executeQuery().next();
        } catch (SQLException ignored) {}
        return false;
    }

    // Find account by account number (for testing)
    public Account findAccountByAccountNo(String accountNo) {
        String sql = "SELECT * FROM accounts WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getString("account_no"),
                        rs.getDouble("balance"),
                        rs.getString("full_name"),
                        rs.getString("username")
                );
            }
        } catch (SQLException e) {
            System.out.println("Failed to find account: " + e.getMessage());
        }
        return null;
    }

    // Update account balance (for testing)
    public boolean updateBalance(String accountNo, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNo);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update balance: " + e.getMessage());
            return false;
        }
    }
}