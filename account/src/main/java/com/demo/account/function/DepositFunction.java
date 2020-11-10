package com.demo.account.function;

import com.demo.account.repository.AccountRepository;
import com.demo.account.repository.AccountTransactionRepository;
import com.demo.cqrs.command.account.DepositCmd;
import com.demo.cqrs.command.account.DepositRes;
import com.demo.cqrs.rpc.RpcFunction;
import org.springframework.beans.factory.annotation.Autowired;

public class DepositFunction implements RpcFunction<DepositCmd, DepositRes> {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Override
    public Class<DepositCmd> type() {
        return DepositCmd.class;
    }

    @Override
    public DepositRes handle(DepositCmd request) throws Exception {
        
        return null;
    }
}
