package com.demo.cqrs.command.account;

import com.demo.cqrs.data.AccountData;
import com.demo.cqrs.exception.ErrorEnum;
import com.demo.cqrs.rpc.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class DepositRes extends Response {

    @Getter
    @RequiredArgsConstructor
    public enum Code implements ErrorEnum {
        ACCOUNT_NOT_FOUND(1001, "account not found"),
        ;

        private final int code;
        private final String reason;
    }

    private AccountData accountData;
}
