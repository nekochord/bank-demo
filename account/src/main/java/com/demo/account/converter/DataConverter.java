package com.demo.account.converter;

import com.demo.account.entity.Account;
import com.demo.account.entity.AccountTransaction;
import com.demo.cqrs.data.AccountData;
import com.demo.cqrs.data.AccountTransactionData;

public interface DataConverter {
    static AccountData toAccountData(Account account) {
        AccountData accountData = new AccountData();
        accountData.setId(account.getId());
        accountData.setName(account.getName());
        accountData.setCreatedDate(account.getCreatedDate());
        accountData.setLastModifiedDate(account.getLastModifiedDate());
        accountData.setBalance(account.getBalance());
        return accountData;
    }

    static AccountTransactionData toAccountTransactionData(AccountTransaction accountTransaction) {
        AccountTransactionData accountTransactionData = new AccountTransactionData();
        accountTransactionData.setId(accountTransaction.getId());
        accountTransactionData.setCreatedDate(accountTransaction.getCreatedDate());
        accountTransactionData.setAmount(accountTransaction.getAmount());
        accountTransactionData.setComment(accountTransaction.getComment());
        return accountTransactionData;
    }
}
