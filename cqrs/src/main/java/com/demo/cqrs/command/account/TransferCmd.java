package com.demo.cqrs.command.account;

import com.demo.cqrs.rpc.Request;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferCmd extends Request {
    @NotNull
    private Long fromAccountId;
    @NotNull
    private Long toAccountId;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotBlank
    private String fromComment;
    @NotBlank
    private String toComment;
}
