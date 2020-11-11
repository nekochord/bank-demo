package com.demo.cqrs.command.account;

import com.demo.cqrs.exception.ErrorEnum;
import com.demo.cqrs.rpc.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TransferRes extends Response {
    @Getter
    @RequiredArgsConstructor
    public enum Code implements ErrorEnum {
        ACCOUNT_NOT_FOUND(1001, "account not found"),
        ACCOUNT_BALANCE_NOT_ENOUGH(1002, "the balance of account is not enough"),
        ;

        private final int code;
        private final String reason;
    }
}
