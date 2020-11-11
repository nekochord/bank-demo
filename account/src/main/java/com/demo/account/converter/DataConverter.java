package com.demo.account.converter;

import com.demo.account.entity.Account;
import com.demo.cqrs.data.AccountData;

public interface DataConverter {
    public static AccountData toAccountData(Account account) {
        AccountData accountData = new AccountData();
        accountData.setId(account.getId());
        accountData.setName(account.getName());
        accountData.setCreatedDate(account.getCreatedDate());
        accountData.setLastModifiedDate(account.getLastModifiedDate());
        accountData.setBalance(account.getBalance());
        return accountData;
    }
}
