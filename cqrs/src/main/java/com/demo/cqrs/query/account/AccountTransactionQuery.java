package com.demo.cqrs.query.account;

import com.demo.cqrs.rpc.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransactionQuery extends Request {
    private Long accountId;
    private Integer page = 0;
    private Integer size = 20;
}
