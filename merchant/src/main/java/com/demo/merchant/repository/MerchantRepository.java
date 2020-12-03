package com.demo.merchant.repository;

import com.demo.merchant.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository {
    public Page<Merchant> findAndOrderByCreatedDateDesc(Pageable pageable);
}
