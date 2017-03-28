package moneyTransfer.storage;

import moneyTransfer.user.Account;
import moneyTransfer.user.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStorage implements Storage {
    private volatile Map<Integer, Account> accounts;
    private final Object monitor;

    public UserStorage() {
        this.accounts = new ConcurrentHashMap<>();
        this.monitor = new Object();
    }

    @Override
    public synchronized boolean addAccount(final Account account) {
        //synchronized (this) {

                final Account a = this.accounts.get(account.getId());
                if (a != null) {
                    System.out.println("UserStorage = " + a);
                    return false;
                }

                this.accounts.put(account.getId(), account);
                return true;

        //}
    }

    @Override
    public Account viewAccount(final Integer id) {
        synchronized (this.monitor) {
            final Account account = this.accounts.get(id);
            if (account != null) {
                return account;
            }

            return new User(new BigDecimal("-1"), "not found", -1);
        }
    }

    @Override
    public boolean updateAccount(final Integer id, final String name) {
        synchronized (this) {
            final Account oldVersion = this.accounts.get(id);
            if (oldVersion != null) {
                final Account updatedAccount = new User(
                        oldVersion.getAmount(), name, id
                );

                this.accounts.put(id, updatedAccount);
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean delAccount(final Integer id) {
        synchronized (this.monitor) {
            final Account account = this.accounts.get(id);
            if (account != null) {
                this.accounts.remove(id);
                return true;
            }

            return false;
        }
    }
}
