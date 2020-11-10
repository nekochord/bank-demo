package com.demo.cqrs.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MerchantData {
    private Long id;
    private Long accountId;
    private String name;
    private Date createdDate;
    private Date lastModifiedDate;
}
