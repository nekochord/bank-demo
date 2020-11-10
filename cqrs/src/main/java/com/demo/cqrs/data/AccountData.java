package com.demo.cqrs.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class AccountData {
    private Long id;
    private String name;
    private Date createdDate;
    private Date lastModifiedDate;
    private BigDecimal balance;
}
