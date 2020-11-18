package com.demo.account.function;

import com.demo.account.converter.DataConverter;
import com.demo.account.entity.Account;
import com.demo.account.entity.AccountTransaction;
import com.demo.account.repository.AccountRepository;
import com.demo.account.repository.AccountTransactionRepository;
import com.demo.cqrs.command.CommandFunction;
import com.demo.cqrs.command.account.DepositCmd;
import com.demo.cqrs.command.account.DepositRes;
import com.demo.cqrs.exception.RpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@Component
public class DepositFunction implements CommandFunction<DepositCmd, DepositRes> {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Override
    @Transactional(rollbackFor = RpcException.class)
    public DepositRes execute(@Validated DepositCmd request) throws RpcException {
        Account account = entityManager.find(Account.class, request.getAccountId(), LockModeType.PESSIMISTIC_WRITE);
        if (account == null) {
            throw DepositRes.Code.ACCOUNT_NOT_FOUND.exception();
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        account = accountRepository.save(account);
        AccountTransaction accountTransaction = newAccountTransaction(account, request);
        accountTransactionRepository.save(accountTransaction);
        DepositRes response = new DepositRes();
        response.setAccountData(DataConverter.toAccountData(account));
        return response;
    }

    private static AccountTransaction newAccountTransaction(Account account, DepositCmd request) {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAccount(account);
        accountTransaction.setAmount(request.getAmount());
        accountTransaction.setComment("deposit");
        return accountTransaction;
    }
}
