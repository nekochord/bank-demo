package com.demo.cqrs.query.account;

import com.demo.cqrs.rpc.Request;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AccountQuery extends Request {
    @NotNull
    private Long accountId;
}
