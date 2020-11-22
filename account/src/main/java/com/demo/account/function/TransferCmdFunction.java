package com.demo.account.function;

import com.demo.account.entity.Account;
import com.demo.account.entity.AccountTransaction;
import com.demo.account.entity.UndoLog;
import com.demo.account.repository.AccountRepository;
import com.demo.account.repository.AccountTransactionRepository;
import com.demo.account.undo.TransferUndo;
import com.demo.cqrs.command.CommandFunction;
import com.demo.cqrs.command.account.TransferCmd;
import com.demo.cqrs.command.account.TransferRes;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;

@Component
public class TransferCmdFunction implements CommandFunction<TransferCmd, TransferRes>,
        UndoConsumer<TransferUndo> {

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
    public TransferRes execute(@Validated TransferCmd request) throws RpcException {
        Account[] fromTo = findFromAndToAccounts(request.getFromAccountId(), request.getToAccountId());

        final Account from = fromTo[0];
        final Account to = fromTo[1];

        BigDecimal amount = request.getAmount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw TransferRes.Code.ACCOUNT_BALANCE_NOT_ENOUGH.exception();
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        AccountTransaction fromTransaction =
                accountTransactionRepository.save(newAccountTransaction(from, amount.negate(), request.getFromComment()));
        AccountTransaction toTransaction =
                accountTransactionRepository.save(newAccountTransaction(to, amount, request.getToComment()));
        saveUndoLog(request.getId(), from.getId(), to.getId(), fromTransaction.getId(), toTransaction.getId());

        return new TransferRes();
    }

    private Account[] findFromAndToAccounts(Long fromAccountId, Long toAccountId) throws RpcException {
        List<Account> accounts = findByIdInWithLock(fromAccountId, toAccountId);

        Account[] fromTo = new Account[2];

        for (Account account : accounts) {
            if (fromAccountId.equals(account.getId())) fromTo[0] = account;
            if (toAccountId.equals(account.getId())) fromTo[1] = account;
        }

        if (fromTo[0] == null || fromTo[1] == null) {
            throw TransferRes.Code.ACCOUNT_NOT_FOUND.exception();
        }

        return fromTo;
    }

    private List<Account> findByIdInWithLock(Long id1, Long id2) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> q = cb.createQuery(Account.class);
        Root<Account> accountRoot = q.from(Account.class);
        q.select(accountRoot).where(accountRoot.get("id").in(id1, id2));
        return entityManager.createQuery(q).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    private void saveUndoLog(String requestId, Long fromAccountId, Long toAccountId,
                             Long fromTransactionId, Long toTransactionId) {
        TransferUndo transferUndo = new TransferUndo();
        transferUndo.setFromAccountId(fromAccountId);
        transferUndo.setToAccountId(toAccountId);
        transferUndo.setFromTransactionId(fromTransactionId);
        transferUndo.setToTransactionId(toTransactionId);

        UndoLog undoLog = new UndoLog();
        undoLog.setRequestId(requestId);
        undoLog.setStatus(UndoLogStatus.FREE);
        undoLog.setUndo(transferUndo);

        undoLogRepository.save(undoLog);
    }

    private static AccountTransaction newAccountTransaction(Account account,
                                                            BigDecimal amount,
                                                            String comment) {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAccount(account);
        accountTransaction.setAmount(amount);
        accountTransaction.setComment(comment);
        return accountTransaction;
    }

    @Override
    public void consume(TransferUndo undo) throws Exception {
        Account[] fromTo = findFromAndToAccounts(undo.getFromAccountId(), undo.getToAccountId());

        final Account from = fromTo[0];
        final Account to = fromTo[1];

        AccountTransaction fromTransaction = accountTransactionRepository.findById(undo.getFromTransactionId())
                .orElseThrow(TransferUndo.Code.ACCOUNT_TRANSACTION_NOT_FOUND::exception);
        AccountTransaction toTransaction = accountTransactionRepository.findById(undo.getToTransactionId())
                .orElseThrow(TransferUndo.Code.ACCOUNT_TRANSACTION_NOT_FOUND::exception);

        BigDecimal fromResult = from.getBalance().subtract(fromTransaction.getAmount());
        BigDecimal toResult = to.getBalance().subtract(toTransaction.getAmount());

        if (fromResult.signum() == -1 || toResult.signum() == -1) {
            throw TransferUndo.Code.ACCOUNT_NOT_ENOUGH.exception();
        }

        from.setBalance(fromResult);
        to.setBalance(toResult);

        accountTransactionRepository.delete(fromTransaction);
        accountTransactionRepository.delete(toTransaction);
    }
}
