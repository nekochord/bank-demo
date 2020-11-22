package com.demo.cqrs.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class AccountTransactionData {
    private Long id;
    private Date createdDate;
    private BigDecimal amount;
    private String comment;
}
