package com.demo.cqrs.command.account;

import com.demo.cqrs.data.AccountData;
import com.demo.cqrs.rpc.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRes extends Response {
    private AccountData accountData;
}
