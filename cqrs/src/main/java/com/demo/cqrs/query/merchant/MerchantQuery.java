package com.demo.cqrs.query.merchant;

import com.demo.cqrs.rpc.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantQuery extends Request {
    private Integer page = 0;
    private Integer size = 20;
}
