package com.demo.account.function;

import com.demo.account.entity.Account;
import com.demo.account.entity.AccountTransaction;
import com.demo.account.repository.AccountRepository;
import com.demo.account.repository.AccountTransactionRepository;
import com.demo.cqrs.command.account.TransferCmd;
import com.demo.cqrs.command.account.TransferRes;
import com.demo.cqrs.exception.RpcException;
import com.demo.cqrs.rpc.RpcFunction;
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
public class TransferFunction implements RpcFunction<TransferCmd, TransferRes> {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Override
    @Transactional(rollbackFor = RpcException.class)
    public TransferRes handle(@Validated TransferCmd request) throws RpcException {
        List<Account> accounts = findByIdInWithLock(request.getFromAccountId(), request.getToAccountId());

        Account from = null;
        Account to = null;
        for (Account account : accounts) {
            if (request.getFromAccountId().equals(account.getId())) from = account;
            if (request.getToAccountId().equals(account.getId())) to = account;
        }

        if (from == null || to == null) {
            throw TransferRes.Code.ACCOUNT_NOT_FOUND.exception();
        }

        BigDecimal amount = request.getAmount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw TransferRes.Code.ACCOUNT_BALANCE_NOT_ENOUGH.exception();
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        String trace = request.getTrace();
        accountTransactionRepository.save(newAccountTransaction(from, amount.negate(), "transfer out", trace));
        accountTransactionRepository.save(newAccountTransaction(to, amount, "transfer in", trace));
        return new TransferRes();
    }

    private List<Account> findByIdInWithLock(Long id1, Long id2) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> q = cb.createQuery(Account.class);
        Root<Account> accountRoot = q.from(Account.class);
        q.select(accountRoot).where(accountRoot.get("id").in(id1, id2));
        return entityManager.createQuery(q).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    private static AccountTransaction newAccountTransaction(Account account,
                                                            BigDecimal amount,
                                                            String comment,
                                                            String trace) {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAccount(account);
        accountTransaction.setAmount(amount);
        accountTransaction.setComment(comment);
        accountTransaction.setTrace(trace);
        return accountTransaction;
    }
}
