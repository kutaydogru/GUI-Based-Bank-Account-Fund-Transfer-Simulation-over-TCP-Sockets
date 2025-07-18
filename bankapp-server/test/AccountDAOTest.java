package test;

import dao.AccountDAO;
import model.Account;

/**
 * This is a test class for AccountDAO.
 * It demonstrates adding, finding, and updating an account.
 */
public class AccountDAOTest {
    public static void main(String[] args) {
        AccountDAO dao = new AccountDAO();

        // Add an account (must use 4 parameters: accountNo, balance, fullName, username)
        Account acc = new Account("12345", 5000.0, "Ali Veli", "kutay");
        boolean inserted = dao.addAccount(acc);
        System.out.println("Was account inserted? " + inserted);

        // Find account by account number
        Account found = dao.findAccountByAccountNo("12345");
        System.out.println("Found account: " + found);

        // Update account balance
        boolean updated = dao.updateBalance("12345", 2500.0);
        System.out.println("Was balance updated? " + updated);

        // Find updated account
        Account updatedAcc = dao.findAccountByAccountNo("12345");
        System.out.println("Updated account: " + updatedAcc);
    }
}