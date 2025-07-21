package server.dao;

import common.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private String url = "jdbc:sqlite:server/db/bank.db";

    public boolean addAccount(Account acc) {
        String sql = "INSERT INTO accounts(account_no, balance, full_name, username) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, acc.getAccountNo());
            stmt.setDouble(2, acc.getBalance());
            stmt.setString(3, acc.getFullName());
            stmt.setString(4, acc.getUsername());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add account: " + e.getMessage());
            return false;
        }
    }

    public List<Account> getAccountsByUsername(String username) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Account(
                        rs.getString("account_no"),
                        rs.getDouble("balance"),
                        rs.getString("full_name"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list accounts: " + e.getMessage());
        }
        return list;
    }

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

    public boolean updateAccountBalance(String accountNo, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update balance: " + e.getMessage());
            return false;
        }
    }
}