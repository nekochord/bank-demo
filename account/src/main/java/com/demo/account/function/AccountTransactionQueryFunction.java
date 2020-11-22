package com.demo.account.function;

import com.demo.account.converter.DataConverter;
import com.demo.account.entity.AccountTransaction;
import com.demo.account.repository.AccountTransactionRepository;
import com.demo.cqrs.query.QueryFunction;
import com.demo.cqrs.query.account.AccountTransactionQuery;
import com.demo.cqrs.query.account.AccountTransactionRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountTransactionQueryFunction implements QueryFunction<AccountTransactionQuery, AccountTransactionRes> {

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountTransactionRes query(AccountTransactionQuery request) throws Exception {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<AccountTransaction> accountTransactionPage =
                accountTransactionRepository.findByAccountIdOrderByCreatedDateDesc(request.getAccountId(), pageRequest);
        AccountTransactionRes res = new AccountTransactionRes();
        res.setAccountTransactionDataList(accountTransactionPage.map(DataConverter::toAccountTransactionData).toList());
        return res;
    }
}
