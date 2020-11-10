package com.demo.cqrs.command.account;

import com.demo.cqrs.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositCmd extends Command {
    private Long accountId;
    private BigDecimal amount;
}
