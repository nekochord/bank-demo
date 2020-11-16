package com.demo.account.function;

import com.demo.account.converter.DataConverter;
import com.demo.account.entity.Account;
import com.demo.account.entity.UndoLog;
import com.demo.account.repository.AccountRepository;
import com.demo.account.undo.CreateAccountUndo;
import com.demo.cqrs.command.account.CreateAccountCmd;
import com.demo.cqrs.command.account.CreateAccountRes;
import com.demo.cqrs.rpc.RpcFunction;
import com.demo.cqrs.undo.UndoConsumer;
import com.demo.cqrs.undo.UndoLogRepository;
import com.demo.cqrs.undo.UndoLogStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateAccountFunction implements RpcFunction<CreateAccountCmd, CreateAccountRes>,
        UndoConsumer<CreateAccountUndo> {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UndoLogRepository undoLogRepository;

    @Override
    @Transactional
    public CreateAccountRes handle(CreateAccountCmd request) {
        Account account = new Account();
        account.setName(request.getName());
        account.setBalance(BigDecimal.ZERO);
        account = accountRepository.save(account);
        saveUndoLog(request.getId(), account.getId());

        CreateAccountRes res = new CreateAccountRes();
        res.setAccountData(DataConverter.toAccountData(account));
        return res;
    }

    private void saveUndoLog(String requestId, Long accountId) {
        CreateAccountUndo createAccountUndo = new CreateAccountUndo();
        createAccountUndo.setAccountId(accountId);

        UndoLog undoLog = new UndoLog();
        undoLog.setRequestId(requestId);
        undoLog.setStatus(UndoLogStatus.FREE);
        undoLog.setUndo(createAccountUndo);

        undoLogRepository.save(undoLog);
    }

    @Override
    @Transactional
    public void consume(CreateAccountUndo undo) throws Exception {
        Long accountId = undo.getAccountId();
        accountRepository.deleteById(accountId);
    }
}
