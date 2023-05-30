package DAO;

import Model.Account;

import Service.AccountService;
import Util.ConnectionUtil;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public List<Account> getAllUserAccount() {
        Connection con = ConnectionUtil.getConnection();
        List<Account> accounts = new ArrayList<>();
        try {
            String sql = "SELECT * FROM account";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Account account = new Account(rs.getInt("account_id"), rs.getString("username"),
                        rs.getString("password"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return accounts;
    }

    public Account getAccountId() {

        return null;
    }

    public Account createAccount(Account account) {
        Connection con = ConnectionUtil.getConnection();
        try {
            String checkUsernameQuery = "SELECT * FROM account WHERE username = ?";
            PreparedStatement checkUsernameStmt = con.prepareStatement(checkUsernameQuery);
            checkUsernameStmt.setString(1, account.getUsername());
            ResultSet rs = checkUsernameStmt.executeQuery();

            if (rs.next()) {
                return null; // account with this username already exists
            }

            String insertAccountQuery = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement insertAccountStmt = con.prepareStatement(insertAccountQuery,
                    Statement.RETURN_GENERATED_KEYS);
            insertAccountStmt.setString(1, account.getUsername());
            insertAccountStmt.setString(2, account.getPassword());
            int affectedRows = insertAccountStmt.executeUpdate();

            if (affectedRows == 0) {
                return null; // account creation failed
            }

            ResultSet generatedKeys = insertAccountStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int accountId = generatedKeys.getInt(1);
                return new Account(accountId, account.getUsername(), account.getPassword());
            } else {
                return null; // account creation failed
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Account getAccountByUsername(String username) {
        Connection con = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Account getAccountByUsernameAndPassword(String username, String password) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM account WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
