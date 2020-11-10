package com.demo.cqrs.command.account;

import com.demo.cqrs.rpc.Response;
import com.demo.cqrs.data.AccountData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositRes extends Response {
    private AccountData accountData;
}
