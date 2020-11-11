package com.demo.cqrs.command.account;

import com.demo.cqrs.command.Command;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
public class DepositCmd extends Command {
    @NotNull
    private Long accountId;
    @NotNull
    @Positive
    private BigDecimal amount;
}
