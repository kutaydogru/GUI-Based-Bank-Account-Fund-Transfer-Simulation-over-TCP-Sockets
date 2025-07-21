package common;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountNo;
    private double balance;
    private String fullName;
    private String username; // Account owner

    public Account(String accountNo, double balance, String fullName, String username) {
        this.accountNo = accountNo;
        this.balance = balance;
        this.fullName = fullName;
        this.username = username;
    }

    public String getAccountNo() { return accountNo; }
    public double getBalance() { return balance; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }

    public void setBalance(double balance) { this.balance = balance; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        return String.format("Account{accountNo='%s', balance=%.2f, owner='%s (%s)'}",
                accountNo, balance, fullName, username);
    }
}