package com.demo.account.undo;

import com.demo.cqrs.exception.ErrorEnum;
import com.demo.cqrs.undo.Undo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class TransferUndo implements Undo {
    @Getter
    @RequiredArgsConstructor
    public enum Code implements ErrorEnum {
        ACCOUNT_NOT_FOUND(1001, "account not found"),
        ACCOUNT_TRANSACTION_NOT_FOUND(1002, "accountTransaction not found"),
        ACCOUNT_NOT_ENOUGH(1003, "account balance is not enough"),
        ;

        private final int code;
        private final String reason;
    }

    private Long fromAccountId;
    private Long toAccountId;
    private Long fromTransactionId;
    private Long toTransactionId;
}
