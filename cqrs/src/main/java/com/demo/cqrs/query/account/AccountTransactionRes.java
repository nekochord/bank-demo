package com.demo.cqrs.query.account;

import com.demo.cqrs.data.AccountTransactionData;
import com.demo.cqrs.rpc.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountTransactionRes extends Response {
    private List<AccountTransactionData> accountTransactionDataList;
}
