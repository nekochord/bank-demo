package com.demo.account.function;

import com.demo.account.converter.DataConverter;
import com.demo.account.entity.Account;
import com.demo.account.repository.AccountRepository;
import com.demo.cqrs.command.account.CreateAccountCmd;
import com.demo.cqrs.command.account.CreateAccountRes;
import com.demo.cqrs.rpc.RpcFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateAccountFunction implements RpcFunction<CreateAccountCmd, CreateAccountRes> {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public CreateAccountRes handle(CreateAccountCmd request) {
        Account account = new Account();
        account.setName(request.getName());
        account.setBalance(BigDecimal.ZERO);
        account = accountRepository.save(account);

        CreateAccountRes res = new CreateAccountRes();
        res.setAccountData(DataConverter.toAccountData(account));
        return res;
    }
}
