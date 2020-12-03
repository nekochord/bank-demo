package com.demo.cqrs.query.merchant;

import com.demo.cqrs.data.MerchantData;
import com.demo.cqrs.rpc.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MerchantRes extends Response {
    List<MerchantData> merchantDataList;
}
