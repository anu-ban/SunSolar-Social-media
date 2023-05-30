package Service;

import java.util.List;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    AccountDAO accountDao;

    public AccountService() {
        this.accountDao = new AccountDAO();
    }

    public List<Account> getAllUserAccount() {
        return accountDao.getAllUserAccount();

    }

    public Account createAccount(Account account) {
        // if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
        // return null;
        // }

        // Account existingAccount =
        // accountDao.getAccountByUsername(account.getUsername());
        // if (existingAccount != null) {
        // return null;
        // }

        return accountDao.createAccount(account);
    }

    public Account getAccountByUsername(String username) {
        return accountDao.getAccountByUsername(username);
    }

    public Account getAccountByUsernameAndPassword(String username, String password) {
        return accountDao.getAccountByUsernameAndPassword(username, password);
    }
}
