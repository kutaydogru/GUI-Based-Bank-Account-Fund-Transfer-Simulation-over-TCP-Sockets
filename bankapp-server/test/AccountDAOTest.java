package test;

import dao.AccountDAO;
import model.Account;

public class AccountDAOTest {
    public static void main(String[] args) {
        AccountDAO dao = new AccountDAO();

        // Hesap ekle
        Account acc = new Account("12345", 5000.0, "Ali Veli");
        boolean inserted = dao.addAccount(acc);
        System.out.println("Hesap eklendi mi? " + inserted);

        // Hesap bul
        Account found = dao.findAccountByAccountNo("12345");
        System.out.println("Bulunan hesap: " + found);

        // Bakiye güncelle
        boolean updated = dao.updateBalance("12345", 2500.0);
        System.out.println("Bakiye güncellendi mi? " + updated);

        // Güncel hesap bilgisi oku
        Account updatedAcc = dao.findAccountByAccountNo("12345");
        System.out.println("Güncel Hesap: " + updatedAcc);
    }
}