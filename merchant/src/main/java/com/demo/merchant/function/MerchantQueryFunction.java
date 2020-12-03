package com.demo.merchant.function;

import com.demo.cqrs.query.QueryFunction;
import com.demo.cqrs.query.merchant.MerchantQuery;
import com.demo.cqrs.query.merchant.MerchantRes;
import com.demo.merchant.converter.DataConverter;
import com.demo.merchant.entity.Merchant;
import com.demo.merchant.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
public class MerchantQueryFunction implements QueryFunction<MerchantQuery, MerchantRes> {

    @Autowired
    private MerchantRepository merchantRepository;

    @Override
    @Transactional(readOnly = true)
    public MerchantRes query(MerchantQuery request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<Merchant> merchantPage = merchantRepository.findAndOrderByCreatedDateDesc(pageRequest);
        MerchantRes res = new MerchantRes();
        res.setMerchantDataList(merchantPage.get().map(DataConverter::toMerchantData).collect(Collectors.toList()));
        return res;
    }
}
