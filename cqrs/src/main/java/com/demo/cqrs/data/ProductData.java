package com.demo.cqrs.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ProductData {
    private Long id;
    private Date createdDate;
    private Date lastModifiedDate;
    private String name;
    private BigDecimal price;
}
