package com.demo.account.function;

import com.demo.account.converter.DataConverter;
import com.demo.account.entity.Account;
import com.demo.account.repository.AccountRepository;
import com.demo.cqrs.query.QueryFunction;
import com.demo.cqrs.query.account.AccountQuery;
import com.demo.cqrs.query.account.AccountRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountQueryFunction implements QueryFunction<AccountQuery, AccountRes> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountRes query(AccountQuery request) throws Exception {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(AccountRes.Code.ACCOUNT_NOT_FOUND::exception);
        AccountRes res = new AccountRes();
        res.setAccountData(DataConverter.toAccountData(account));
        return res;
    }
}
