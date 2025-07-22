package server.services;

import common.Account;
import server.dao.AccountDAO;

import java.util.List;

public class TransactionService {
    private AccountDAO accountDAO;

    public TransactionService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public List<Account> listAccounts(String username) {
        return accountDAO.getAccountsByUsername(username);
    }

    public Account createAccount(String accountNo, double balance, String fullName, String username) {
        Account account = new Account(accountNo, balance, fullName, username);
        boolean ok = accountDAO.addAccount(account);
        return ok ? account : null;
    }

    // Withdraw money from user's account
    public String withdraw(String accountNo, double amount, String requestingUsername) {
        Account account = accountDAO.findAccountByAccountNo(accountNo);
        if (account == null) return "ERROR:notfound";
        if (!account.getUsername().equals(requestingUsername)) return "ERROR:notyouraccount";
        if (amount <= 0) return "ERROR:invalidamount";
        if (account.getBalance() < amount) return "ERROR:balance";
        accountDAO.updateAccountBalance(accountNo, account.getBalance() - amount);
        return "OK";
    }

    // Deposit money to user's account
    public String deposit(String accountNo, double amount, String requestingUsername) {
        Account account = accountDAO.findAccountByAccountNo(accountNo);
        if (account == null) return "ERROR:notfound";
        if (!account.getUsername().equals(requestingUsername)) return "ERROR:notyouraccount";
        if (amount <= 0) return "ERROR:invalidamount";
        accountDAO.updateAccountBalance(accountNo, account.getBalance() + amount);
        return "OK";
    }

    // YENİ: account ownership ve güvenlik kontrolü
    public String transfer(String fromAccNo, String toAccNo, double amount, String requestingUsername) {
        Account from = accountDAO.findAccountByAccountNo(fromAccNo);
        Account to = accountDAO.findAccountByAccountNo(toAccNo);
        // Account ownership kontrolü!
        if (from == null || to == null) return "ERROR:notfound";
        if (!from.getUsername().equals(requestingUsername)) return "ERROR:notyouraccount";
        if (from.getBalance() < amount) return "ERROR:balance";
        // update balances
        accountDAO.updateAccountBalance(fromAccNo, from.getBalance() - amount);
        accountDAO.updateAccountBalance(toAccNo, to.getBalance() + amount);
        return "OK";
    }

    // Güvenli balance sorgusu: başka kullanıcı kendi olmayan hesabı göremeyecek.
    public String getBalance(String accountNo, String requestingUsername) {
        Account account = accountDAO.findAccountByAccountNo(accountNo);
        if (account == null) return "ERROR:notfound";
        if (!account.getUsername().equals(requestingUsername))
            return "ERROR:notyouraccount";
        return String.valueOf(account.getBalance());
    }
}