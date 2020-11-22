package com.demo.account.function;

import com.demo.account.converter.DataConverter;
import com.demo.account.entity.Account;
import com.demo.account.entity.AccountTransaction;
import com.demo.account.entity.UndoLog;
import com.demo.account.repository.AccountRepository;
import com.demo.account.repository.AccountTransactionRepository;
import com.demo.account.undo.DepositUndo;
import com.demo.cqrs.command.CommandFunction;
import com.demo.cqrs.command.account.DepositCmd;
import com.demo.cqrs.command.account.DepositRes;
import com.demo.cqrs.exception.RpcException;
import com.demo.cqrs.undo.UndoConsumer;
import com.demo.cqrs.undo.UndoLogRepository;
import com.demo.cqrs.undo.UndoLogStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@Component
public class DepositCmdFunction implements CommandFunction<DepositCmd, DepositRes>,
        UndoConsumer<DepositUndo> {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTransactionRepository accountTransactionRepository;
    @Autowired
    private UndoLogRepository undoLogRepository;

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
        accountTransaction = accountTransactionRepository.save(accountTransaction);
        saveUndoLog(request.getId(), account.getId(), accountTransaction.getId());

        DepositRes response = new DepositRes();
        response.setAccountData(DataConverter.toAccountData(account));
        return response;
    }

    private void saveUndoLog(String requestId, Long accountId, Long accountTransactionId) {
        DepositUndo depositUndo = new DepositUndo();
        depositUndo.setAccountId(accountId);
        depositUndo.setAccountTransactionId(accountTransactionId);

        UndoLog undoLog = new UndoLog();
        undoLog.setRequestId(requestId);
        undoLog.setStatus(UndoLogStatus.FREE);
        undoLog.setUndo(depositUndo);

        undoLogRepository.save(undoLog);
    }

    private static AccountTransaction newAccountTransaction(Account account, DepositCmd request) {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAccount(account);
        accountTransaction.setAmount(request.getAmount());
        accountTransaction.setComment(request.getComment());
        return accountTransaction;
    }

    @Override
    public void consume(DepositUndo undo) throws Exception {
        Account account = entityManager.find(Account.class, undo.getAccountId(), LockModeType.PESSIMISTIC_WRITE);
        if (account == null) {
            throw DepositUndo.Code.ACCOUNT_NOT_FOUND.exception();
        }
        AccountTransaction accountTransaction =
                accountTransactionRepository.findById(undo.getAccountTransactionId())
                        .orElseThrow(DepositUndo.Code.ACCOUNT_TRANSACTION_NOT_FOUND::exception);
        BigDecimal amount = accountTransaction.getAmount();
        BigDecimal result = account.getBalance().subtract(amount);

        if (result.signum() == -1) {
            throw DepositUndo.Code.ACCOUNT_NOT_ENOUGH.exception();
        }

        account.setBalance(result);
        accountRepository.save(account);
        accountTransactionRepository.delete(accountTransaction);
    }
}
