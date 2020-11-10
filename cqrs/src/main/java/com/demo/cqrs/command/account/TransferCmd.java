package com.demo.cqrs.command.account;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferCmd {
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
